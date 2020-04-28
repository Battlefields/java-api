package io.github.tastac.bfj;

import com.google.gson.*;
import io.github.tastac.bfj.components.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
    private final Map<String, Long> timeStamps;
    private final Map<String, Object> cache;

    public BattlefieldsApiImpl(ExecutorService requestPool, Consumer<Exception> exceptionConsumer, long shutdownTimeout, TimeUnit shutdownTimeoutUnit, long cacheTime, TimeUnit cacheTimeUnit)
    {
        this.requestPool = requestPool;
        this.exceptionConsumer = exceptionConsumer;
        this.shutdownTimeout = shutdownTimeout;
        this.shutdownTimeoutUnit = shutdownTimeoutUnit;
        this.cacheTime = cacheTime;
        this.cacheTimeUnit = cacheTimeUnit;
        this.timeStamps = new ConcurrentHashMap<>();
        this.cache = new ConcurrentHashMap<>();
    }

    private static JsonElement request(String url) throws IOException, JsonParseException
    {
        try (CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).build())
        {
            HttpGet get = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(get))
            {
                return JsonParser.parseString(EntityUtils.toString(response.getEntity()));
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

    private static String resolveQueries(String[] queries)
    {
        if (queries.length == 0)
            return "";
        StringBuilder builder = new StringBuilder();
        for (String query : queries)
            builder.append("&").append(StringEscapeUtils.escapeJava(query));
        return builder.toString();
    }

    private boolean isCacheValid(String field)
    {
        return this.timeStamps.containsKey(field) && System.currentTimeMillis() - this.timeStamps.get(field) < TimeUnit.MILLISECONDS.convert(this.cacheTime, this.cacheTimeUnit);
    }

    @Override
    public void clearCache()
    {
        this.timeStamps.clear();
        this.cache.clear();
    }

    @Nullable
    @Override
    public JsonArray get(BattlefieldsApiTable table, String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("custom-" + query))
            return (JsonArray) this.cache.get("custom-" + query);
        JsonArray data;
        try
        {
            data = requestDetail(getRequestUrl(table, query));
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            data = null;
        }
        this.timeStamps.put("custom-" + query, System.currentTimeMillis());
        this.cache.put("custom-" + query, data);
        return data;
    }

    @Override
    public String getServerStatus()
    {
        if (this.isCacheValid("serverStatus"))
            return (String) this.cache.get("serverStatus");
        String serverStatus;
        try
        {
            serverStatus = requestDetail(BFJ.BF_SERVER_STATUS_URL).get(0).getAsJsonObject().get(BFJ.BF_SERVER_HOSTNAME).getAsString();
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            serverStatus = "red";
        }
        this.timeStamps.put("serverStatus", System.currentTimeMillis());
        this.cache.put("serverStatus", serverStatus);
        return serverStatus;
    }

    @Nullable
    @Override
    public BFServerInfo getServerInfo()
    {
        if (this.isCacheValid("serverInfo"))
            return (BFServerInfo) this.cache.get("serverInfo");
        BFServerInfo serverInfo;
        try
        {
            serverInfo = GSON.fromJson(request(BFJ.BF_SERVER_INFO_URL), BFServerInfo.class);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            serverInfo = null;
        }
        this.timeStamps.put("serverInfo", System.currentTimeMillis());
        this.cache.put("serverInfo", serverInfo);
        return serverInfo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<String, Integer>[] getKills(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("kills-" + query))
            return (Pair<String, Integer>[]) this.cache.get("kills-" + query);
        Pair<String, Integer>[] kills;
        try
        {
            JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.KILLS, query));
            kills = new Pair[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++)
            {
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                kills[i] = new ImmutablePair<>(object.get("uuid").getAsString(), object.get("kills").getAsInt());
            }
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            kills = new Pair[0];
        }
        this.timeStamps.put("kills-" + query, System.currentTimeMillis());
        this.cache.put("kills-" + query, kills);
        return kills;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<String, Integer>[] getWins(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("wins-" + query))
            return (Pair<String, Integer>[]) this.cache.get("wins-" + query);
        Pair<String, Integer>[] wins;
        try
        {
            JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.WINS, query));
            wins = new Pair[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++)
            {
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                wins[i] = new ImmutablePair<>(object.get("uuid").getAsString(), object.get("wins").getAsInt());
            }
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            wins = new Pair[0];
        }
        this.timeStamps.put("wins-" + query, System.currentTimeMillis());
        this.cache.put("wins-" + query, wins);
        return wins;
    }

    @Override
    public BFPlayer[] getPlayers(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("players-" + query))
            return (BFPlayer[]) this.cache.get("players-" + query);
        BFPlayer[] players;
        try
        {
            players = GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.PLAYERS, query)), BFPlayer[].class);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            players = new BFPlayer[0];
        }
        this.timeStamps.put("players-" + query, System.currentTimeMillis());
        this.cache.put("players-" + query, players);
        return players;
    }

    @Override
    public BFMatch[] getMatches(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("matches-" + query))
            return (BFMatch[]) this.cache.get("matches-" + query);
        BFMatch[] matches;
        try
        {
            matches = GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.MATCHES, query)), BFMatch[].class);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            matches = new BFMatch[0];
        }
        this.timeStamps.put("matches-" + query, System.currentTimeMillis());
        this.cache.put("matches-" + query, matches);
        return matches;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<String, Integer>[] getOwnedAccessories(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("owned_accessories-" + query))
            return (Pair<String, Integer>[]) this.cache.get("owned_accessories-" + query);
        Pair<String, Integer>[] ownedAccessories;
        try
        {
            JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.OWNED_ACCESSORIES, query));
            ownedAccessories = new Pair[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++)
            {
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                ownedAccessories[i] = new ImmutablePair<>(object.get("uuid").getAsString(), object.get("accessory_id").getAsInt());
            }
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            ownedAccessories = new Pair[0];
        }
        this.timeStamps.put("owned_accessories-" + query, System.currentTimeMillis());
        this.cache.put("owned_accessories-" + query, ownedAccessories);
        return ownedAccessories;
    }

    @Override
    public BFAccessory[] getAccessories(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("accessories-" + query))
            return (BFAccessory[]) this.cache.get("accessories-" + query);
        BFAccessory[] accessories;
        try
        {
            accessories = GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.ACCESSORIES, query)), BFAccessory[].class);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            accessories = new BFAccessory[0];
        }
        this.timeStamps.put("accessories-" + query, System.currentTimeMillis());
        this.cache.put("accessories-" + query, accessories);
        return accessories;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<Integer, String>[] getAccessoryTypes(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("accessory_types-" + query))
            return (Pair<Integer, String>[]) this.cache.get("accessory_types-" + query);
        Pair<Integer, String>[] accessoryTypes;
        try
        {
            JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.ACCESSORY_TYPES, query));
            accessoryTypes = new Pair[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++)
            {
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                accessoryTypes[i] = new ImmutablePair<>(object.get("id").getAsInt(), object.get("name").getAsString());
            }
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            accessoryTypes = new Pair[0];
        }
        this.timeStamps.put("accessory_types-" + query, System.currentTimeMillis());
        this.cache.put("accessory_types-" + query, accessoryTypes);
        return accessoryTypes;
    }

    @Override
    public BFWeapon[] getWeapons(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("weapons-" + query))
            return (BFWeapon[]) this.cache.get("weapons-" + query);
        BFWeapon[] weapons;
        try
        {
            weapons = GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.WEAPONS, query)), BFWeapon[].class);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            weapons = new BFWeapon[0];
        }
        this.timeStamps.put("weapons-" + query, System.currentTimeMillis());
        this.cache.put("weapons-" + query, weapons);
        return weapons;
    }

    @Override
    public BFWeaponStats[] getWeaponStats(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("weapon_stats-" + query))
            return (BFWeaponStats[]) this.cache.get("weapon_stats-" + query);
        BFWeaponStats[] weaponStats;
        try
        {
            weaponStats = GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.WEAPON_STATS, query)), BFWeaponStats[].class);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            weaponStats = new BFWeaponStats[0];
        }
        this.timeStamps.put("weapon_stats-" + query, System.currentTimeMillis());
        this.cache.put("weapon_stats-" + query, weaponStats);
        return weaponStats;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<Integer, Integer>[] getMatchParticipants(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("match_participants-" + query))
            return (Pair<Integer, Integer>[]) this.cache.get("match_participants-" + query);
        Pair<Integer, Integer>[] matchParticipants;
        try
        {
            JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.MATCH_PARTICIPANTS, query));
            matchParticipants = new Pair[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++)
            {
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                matchParticipants[i] = new ImmutablePair<>(object.get("match_id").getAsInt(), object.get("player_id").getAsInt());
            }
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            matchParticipants = new Pair[0];
        }
        this.timeStamps.put("match_participants-" + query, System.currentTimeMillis());
        this.cache.put("match_participants-" + query, matchParticipants);
        return matchParticipants;
    }

    @Override
    public BFKill[] getMatchKills(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("match_kills-" + query))
            return (BFKill[]) this.cache.get("match_kills-" + query);
        BFKill[] matchKills;
        try
        {
            matchKills = GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.MATCH_KILLS, query)), BFKill[].class);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            matchKills = new BFKill[0];
        }
        this.timeStamps.put("match_kills-" + query, System.currentTimeMillis());
        this.cache.put("match_kills-" + query, matchKills);
        return matchKills;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<String, Integer>[] getOwnedEmotes(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("owned_emotes-" + query))
            return (Pair<String, Integer>[]) this.cache.get("owned_emotes-" + query);
        Pair<String, Integer>[] ownedEmotes;
        try
        {
            JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.OWNED_EMOTES, query));
            ownedEmotes = new Pair[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++)
            {
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                ownedEmotes[i] = new ImmutablePair<>(object.get("uuid").getAsString(), object.get("emote_id").getAsInt());
            }
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            ownedEmotes = new Pair[0];
        }
        this.timeStamps.put("owned_emotes-" + query, System.currentTimeMillis());
        this.cache.put("owned_emotes-" + query, ownedEmotes);
        return ownedEmotes;
    }

    @Override
    public BFEmote[] getEmotes(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("emotes-" + query))
            return (BFEmote[]) this.cache.get("emotes-" + query);
        BFEmote[] emotes;
        try
        {
            emotes = GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsApiTable.EMOTES, query)), BFEmote[].class);
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            emotes = new BFEmote[0];
        }
        this.timeStamps.put("emotes-" + query, System.currentTimeMillis());
        this.cache.put("emotes-" + query, emotes);
        return emotes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<String, Long>[] getLinkedDiscord(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("linked_discords-" + query))
            return (Pair<String, Long>[]) this.cache.get("linked_discords-" + query);
        Pair<String, Long>[] linkedDiscords;
        try
        {
            JsonArray jsonArray = requestDetail(getRequestUrl(BattlefieldsApiTable.LINKED_DISCORD, query));
            linkedDiscords = new Pair[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++)
            {
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                linkedDiscords[i] = new ImmutablePair<>(object.get("uuid").getAsString(), object.get("discord_id").getAsLong());
            }
        }
        catch (Exception e)
        {
            this.exceptionConsumer.accept(e);
            linkedDiscords = new Pair[0];
        }
        this.timeStamps.put("linked_discords-" + query, System.currentTimeMillis());
        this.cache.put("linked_discords-" + query, linkedDiscords);
        return linkedDiscords;
    }

    //    public static BFKill[] getKillsBySourceID(int sourceID) { return getKillObjFromJson(getJSONFromQuery("match_kills", "source_player", Integer.toString(sourceID))); }
    //
    //    public static BFKill[] getKillsByTargetID(int targetID) { return getKillObjFromJson(getJSONFromQuery("match_kills", "target_player", Integer.toString(targetID))); }
    //
    //    public static BFKill[] getKillsByBFPlayerSource(BFPlayer sourcePlayer) { return getKillsBySourceID(sourcePlayer.getID()); }
    //
    //    public static BFKill[] getKillsByBFPlayerTarget(BFPlayer targetPlayer) { return getKillsBySourceID(targetPlayer.getID()); }
    //
    //    public static BFKill[] getAllKills() { return getKillObjFromJson(getJSONFromTable("match_kills")); }

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
}
