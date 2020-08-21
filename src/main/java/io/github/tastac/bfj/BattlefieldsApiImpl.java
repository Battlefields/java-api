package io.github.tastac.bfj;

import com.google.gson.*;
import io.github.tastac.bfj.components.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>Internal implementation of {@link BattlefieldsApi}.</p>
 *
 * @author Tastac, Ocelot
 */
public class BattlefieldsApiImpl implements BattlefieldsApi
{
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(BFServerInfo.class, new BFServerInfo.Deserializer()).create();
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    private final ExecutorService requestPool;
    private final Consumer<Exception> exceptionConsumer;
    private final long shutdownTimeout;
    private final TimeUnit shutdownTimeoutUnit;
    private final long cacheTime;
    private final TimeUnit cacheTimeUnit;
    private final boolean cacheErrors;
    private final Map<String, Long> timeStamps;
    private final Map<String, Object> cache;
    private final Map<String, Long> errorCache;

    public BattlefieldsApiImpl(ExecutorService requestPool, Consumer<Exception> exceptionConsumer, long shutdownTimeout, TimeUnit shutdownTimeoutUnit, long cacheTime, TimeUnit cacheTimeUnit, boolean cacheErrors)
    {
        this.requestPool = requestPool;
        this.exceptionConsumer = exceptionConsumer;
        this.shutdownTimeout = shutdownTimeout;
        this.shutdownTimeoutUnit = shutdownTimeoutUnit;
        this.cacheTime = cacheTime;
        this.cacheTimeUnit = cacheTimeUnit;
        this.cacheErrors = cacheErrors;
        this.timeStamps = new ConcurrentHashMap<>();
        this.cache = new ConcurrentHashMap<>();
        this.errorCache = new ConcurrentHashMap<>();
    }

    private static byte[] requestRaw(String url) throws IOException
    {
        try (CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).build())
        {
            HttpGet get = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(get))
            {
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() != 200)
                    throw new IOException("Failed to connect to '" + url + "'. " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
                return EntityUtils.toByteArray(response.getEntity());
            }
        }
    }

    private static JsonElement request(String url) throws IOException
    {
        try (CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).build())
        {
            HttpGet get = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(get))
            {
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() != 200)
                    throw new IOException("Failed to connect to '" + url + "'. " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
                return new JsonParser().parse(EntityUtils.toString(response.getEntity()));
            }
        }
    }

    private static JsonArray requestDetail(String url) throws IOException, JsonParseException
    {
        JsonObject requestObject = request(url).getAsJsonObject();
        if (!requestObject.get("status").getAsBoolean())
            throw new IOException("Failed to connect to Battlefields API: " + requestObject.get("detail").getAsString());
        return requestObject.get("detail").getAsJsonArray();
    }

    private static String getRequestUrl(BattlefieldsApiTable table, String query)
    {
        return String.format(BFJ.BF_API_URL + "?type=%s%s", table.getTable(), query);
    }

    private static String resolveQueries(String[] queries) throws IOException
    {
        if (queries.length == 0)
            return "";
        StringBuilder builder = new StringBuilder();
        for (String query : queries)
        {
            String[] splitQuery = query.split("=", 2);
            if (splitQuery.length != 2)
                throw new IOException("Invalid query: " + query);
            builder.append("&");
            builder.append(URLEncoder.encode(splitQuery[0], StandardCharsets.UTF_8.toString()));
            builder.append("=");
            builder.append(URLEncoder.encode(splitQuery[1], StandardCharsets.UTF_8.toString()));
        }
        return builder.toString();
    }

    private boolean isCacheValid(String field)
    {
        if (this.cacheTime <= 0)
            return false;
        if (!this.timeStamps.containsKey(field) && (!this.cacheErrors || !this.errorCache.containsKey(field)))
            return false;
        long timeStamp = this.timeStamps.containsKey(field) ? this.timeStamps.get(field) : this.errorCache.get(field);
        return System.currentTimeMillis() - timeStamp < TimeUnit.MILLISECONDS.convert(this.cacheTime, this.cacheTimeUnit);
    }

    @SuppressWarnings("unchecked")
    private <T> T retrieve(String field, Fetcher<T> fetcher, Supplier<T> defaultValue)
    {
        if (this.isCacheValid(field))
        {
            if (this.cache.containsKey(field))
            {
                try
                {
                    return (T) this.cache.get(field);
                }
                catch (Exception e)
                {
                    this.exceptionConsumer.accept(e);
                    this.timeStamps.remove(field);
                    this.cache.remove(field);
                }
            }
            else
            {
                return defaultValue.get();
            }
        }

        try
        {
            T value = fetcher.fetch();
            if (this.cacheTime > 0)
            {
                this.timeStamps.put(field, System.currentTimeMillis());
                this.cache.put(field, value);
            }
            return value;
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            if (this.cacheTime > 0 && this.cacheErrors)
                this.errorCache.put(field, System.currentTimeMillis());
            return defaultValue.get();
        }
    }

    @Override
    public void clearCache()
    {
        this.timeStamps.clear();
        this.cache.clear();
        this.errorCache.clear();
    }

    @Nullable
    @Override
    public JsonArray get(BattlefieldsApiTable table, String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("custom-" + query, () -> requestDetail(getRequestUrl(table, query)), () -> null);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return null;
        }
    }

    @Nullable
    @Override
    public JsonObject getCosmeticModel(String modelName)
    {
        try
        {
            return this.retrieve("cosmetic_model", () -> new JsonParser().parse(new String(requestRaw(BFJ.BF_COSMETIC_MODEL_URL + modelName + ".json"))).getAsJsonObject(), () -> null);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return null;
        }
    }

    @Nullable
    @Override
    public String getCosmeticModelHash(String modelName)
    {
        try
        {
            return this.retrieve("cosmetic_model_hash", () -> new String(requestRaw(BFJ.BF_COSMETIC_MODEL_URL + modelName + ".json.md5")), () -> null);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return null;
        }
    }

    @Nullable
    @Override
    public byte[] getCosmeticTexture(String textureName)
    {
        try
        {
            return this.retrieve("cosmetic_texture", () -> requestRaw(BFJ.BF_COSMETIC_TEXTURE_URL + textureName + ".png"), () -> null);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return null;
        }
    }

    @Nullable
    @Override
    public String getCosmeticTextureHash(String textureName)
    {
        try
        {
            return this.retrieve("cosmetic_texture_hash", () -> new String(requestRaw(BFJ.BF_COSMETIC_TEXTURE_URL + textureName + ".png.md5")), () -> null);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return null;
        }
    }

    @Override
    public String getServerStatus()
    {
        return this.retrieve("server_status", () -> requestDetail(BFJ.BF_SERVER_STATUS_URL).get(0).getAsJsonObject().get(BFJ.BF_SERVER_HOSTNAME).getAsString(), () -> "red");
    }

    @Nullable
    @Override
    public BFServerInfo getServerInfo()
    {
        return this.retrieve("server_info", () -> GSON.fromJson(request(BFJ.BF_SERVER_INFO_URL), BFServerInfo.class), null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<String, Integer>[] getKills(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("kills-" + query, () ->
            {
                JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.KILLS, query));
                Pair<String, Integer>[] kills = new Pair[jsonArray.size()];
                for (int i = 0; i < jsonArray.size(); i++)
                {
                    JsonObject object = jsonArray.get(i).getAsJsonObject();
                    kills[i] = new ImmutablePair<>(object.get("uuid").getAsString(), object.get("kills").getAsInt());
                }
                return kills;
            }, () -> new Pair[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new Pair[0];
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<String, Integer>[] getWins(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("wins-" + query, () ->
            {
                JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.WINS, query));
                Pair<String, Integer>[] wins = new Pair[jsonArray.size()];
                for (int i = 0; i < jsonArray.size(); i++)
                {
                    JsonObject object = jsonArray.get(i).getAsJsonObject();
                    wins[i] = new ImmutablePair<>(object.get("uuid").getAsString(), object.get("wins").getAsInt());
                }
                return wins;
            }, () -> new Pair[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new Pair[0];
        }
    }

    @Override
    public BFPlayer[] getPlayers(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("players-" + query, () -> GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.PLAYERS, query)), BFPlayer[].class), () -> new BFPlayer[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new BFPlayer[0];
        }
    }

    @Override
    public BFMatch[] getMatches(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("matches-" + query, () -> GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.MATCHES, query)), BFMatch[].class), () -> new BFMatch[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new BFMatch[0];
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<String, Integer>[] getOwnedAccessories(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("owned_accessories-" + query, () ->
            {
                JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.OWNED_ACCESSORIES, query));
                Pair<String, Integer>[] ownedAccessories = new Pair[jsonArray.size()];
                for (int i = 0; i < jsonArray.size(); i++)
                {
                    JsonObject object = jsonArray.get(i).getAsJsonObject();
                    ownedAccessories[i] = new ImmutablePair<>(object.get("uuid").getAsString(), object.get("accessory_id").getAsInt());
                }
                return ownedAccessories;
            }, () -> new Pair[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new Pair[0];
        }
    }

    @Override
    public BFAccessory[] getAccessories(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("accessories-" + query, () -> GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.ACCESSORIES, query)), BFAccessory[].class), () -> new BFAccessory[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new BFAccessory[0];
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<Integer, String>[] getAccessoryTypes(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("accessory_types-" + query, () ->
            {
                JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.ACCESSORY_TYPES, query));
                Pair<Integer, String>[] accessoryTypes = new Pair[jsonArray.size()];
                for (int i = 0; i < jsonArray.size(); i++)
                {
                    JsonObject object = jsonArray.get(i).getAsJsonObject();
                    accessoryTypes[i] = new ImmutablePair<>(object.get("id").getAsInt(), object.get("name").getAsString());
                }
                return accessoryTypes;
            }, () -> new Pair[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new Pair[0];
        }
    }

    @Override
    public BFWeapon[] getWeapons(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("weapons-" + query, () -> GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.WEAPONS, query)), BFWeapon[].class), () -> new BFWeapon[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new BFWeapon[0];
        }
    }

    @Override
    public BFWeaponStats[] getWeaponStats(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("weapon_stats-" + query, () -> GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.WEAPON_STATS, query)), BFWeaponStats[].class), () -> new BFWeaponStats[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new BFWeaponStats[0];
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<Integer, Integer>[] getMatchParticipants(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("match_participants-" + query, () ->
            {
                JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.MATCH_PARTICIPANTS, query));
                Pair<Integer, Integer>[] matchParticipants = new Pair[jsonArray.size()];
                for (int i = 0; i < jsonArray.size(); i++)
                {
                    JsonObject object = jsonArray.get(i).getAsJsonObject();
                    matchParticipants[i] = new ImmutablePair<>(object.get("match_id").getAsInt(), object.get("player_id").getAsInt());
                }
                return matchParticipants;
            }, () -> new Pair[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new Pair[0];
        }
    }

    @Override
    public BFKill[] getMatchKills(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("match_kills-" + query, () -> GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.MATCH_KILLS, query)), BFKill[].class), () -> new BFKill[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new BFKill[0];
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<String, Integer>[] getOwnedEmotes(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("owned_emotes-" + query, () ->
            {
                JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.OWNED_EMOTES, query));
                Pair<String, Integer>[] ownedEmotes = new Pair[jsonArray.size()];
                for (int i = 0; i < jsonArray.size(); i++)
                {
                    JsonObject object = jsonArray.get(i).getAsJsonObject();
                    ownedEmotes[i] = new ImmutablePair<>(object.get("uuid").getAsString(), object.get("emote_id").getAsInt());
                }
                return ownedEmotes;
            }, () -> new Pair[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new Pair[0];
        }
    }

    @Override
    public BFEmote[] getEmotes(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("emotes-" + query, () -> GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.EMOTES, query)), BFEmote[].class), () -> new BFEmote[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new BFEmote[0];
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<String, Long>[] getLinkedDiscord(String... queries)
    {
        try
        {
            String query = resolveQueries(queries);
            return this.retrieve("linked_discords-" + query, () ->
            {
                JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.LINKED_DISCORD, query));
                Pair<String, Long>[] linkedDiscords = new Pair[jsonArray.size()];
                for (int i = 0; i < jsonArray.size(); i++)
                {
                    JsonObject object = jsonArray.get(i).getAsJsonObject();
                    linkedDiscords[i] = new ImmutablePair<>(object.get("uuid").getAsString(), object.get("discord_id").getAsLong());
                }
                return linkedDiscords;
            }, () -> new Pair[0]);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            return new Pair[0];
        }
    }

    @Override
    public ExecutorService getExecutor()
    {
        return requestPool;
    }

    @Override
    public boolean shutdown() throws InterruptedException
    {
        this.requestPool.shutdown();
        return this.requestPool.awaitTermination(this.shutdownTimeout, this.shutdownTimeoutUnit);
    }

    /**
     * Fetches data from a server.
     *
     * @param <T> The type of data to fetch
     * @author Ocelot
     */
    private interface Fetcher<T>
    {
        /**
         * @return The data read
         * @throws IOException If the data could not be read for any reason
         */
        T fetch() throws IOException;
    }
}
