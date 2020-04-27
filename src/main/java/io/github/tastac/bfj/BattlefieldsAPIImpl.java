package io.github.tastac.bfj;

import com.google.gson.*;
import io.github.tastac.bfj.components.BFServerInfo;
import io.github.tastac.bfj.components.BFWeapon;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * <p>Internal implementation of {@link BattlefieldsAPI}.</p>
 *
 * @author Tastac, Ocelot
 */
public class BattlefieldsAPIImpl implements BattlefieldsAPI
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

    public BattlefieldsAPIImpl(ExecutorService requestPool, Consumer<Exception> exceptionConsumer, long shutdownTimeout, TimeUnit shutdownTimeoutUnit, long cacheTime, TimeUnit cacheTimeUnit)
    {
        this.requestPool = requestPool;
        this.exceptionConsumer = exceptionConsumer;
        this.shutdownTimeout = shutdownTimeout;
        this.shutdownTimeoutUnit = shutdownTimeoutUnit;
        this.cacheTime = cacheTime;
        this.cacheTimeUnit = cacheTimeUnit;
        this.timeStamps = new HashMap<>();
        this.cache = new HashMap<>();
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

    private static String getRequestUrl(BattlefieldsAPITable table, String query)
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

    @Override
    public BFWeapon[] getWeapons(String... queries)
    {
        String query = resolveQueries(queries);
        if (this.isCacheValid("weapons-" + query))
            return (BFWeapon[]) this.cache.get("weaponById-" + query);
        BFWeapon[] weapons;
        try
        {
            weapons = GSON.fromJson(requestDetail(getRequestUrl(BattlefieldsAPITable.WEAPONS, query)), BFWeapon[].class);
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