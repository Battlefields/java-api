package io.github.tastac.bfj;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.tastac.bfj.components.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Deprecated
public class DataRetriever
{
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static final String apiURL = "https://api.battlefieldsmc.net/api/?type=";
    private static final DateTimeFormatter matchDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //TODO add a map of previously retrieved players for quick access later (a cache) include expire timer

    public static JsonElement getJSONFromRequestBuilder(RequestBuilder requestBuilder) { return requestFromURL(apiURL + requestBuilder.getString()); }

    public static JsonElement getJSONFromQuery(String table, String dataID, String query) { return requestFromURL(apiURL + table + "&" + dataID + "=" + query); }

    public static JsonElement getJSONFromTable(String table) { return requestFromURL(apiURL + table); }

    private static JsonElement requestFromURL(String url)
    {
        HttpGet request = new HttpGet(url);

        request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        try (CloseableHttpResponse response = httpClient.execute(request))
        {
            HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                JsonElement object = JsonParser.parseString(EntityUtils.toString(entity)).getAsJsonObject().get("detail");
                return object;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    //TODO Add documentation to all of these methods

    // ########## SERVER STATUS ##########

    public static String getServerStatus()
    {
        HttpGet request = new HttpGet("https://api.battlefieldsmc.net/api/status/");

        request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        try (CloseableHttpResponse response = httpClient.execute(request))
        {
            HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                return JsonParser.parseString(
                        EntityUtils.toString(entity))
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("us-east.battlefieldsmc.net")
                        .getAsString();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return "red";
    }

    public static int getOnlinePlayerCount()
    {
        HttpGet request = new HttpGet("https://api.mcsrvstat.us/2/us-east.battlefieldsmc.net");

        request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        try (CloseableHttpResponse response = httpClient.execute(request))
        {
            HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                return JsonParser.parseString(EntityUtils.toString(entity))
                        .getAsJsonObject()
                        .get("players")
                        .getAsJsonObject()
                        .get("online")
                        .getAsInt();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    // ########## WEAPONS ##########

    private static BFWeapon getWeaponFromJson(JsonElement weaponJson)
    {
        try
        {
            JsonObject weapon = weaponJson.getAsJsonArray().get(0).getAsJsonObject();

            return new BFWeapon(
                    weapon.get("id").getAsInt(),
                    weapon.get("item_id").getAsInt(),
                    weapon.get("item_name").getAsString());
        }
        catch (IllegalStateException e)
        {
            return new BFWeapon(0, 0, "");
        }
    }

    public static BFWeapon getWeaponByID(int ID) { return getWeaponFromJson(getJSONFromQuery("weapons", "id", Integer.toString(ID))); }

    public static BFWeapon getWeaponByItemID(int itemID) { return getWeaponFromJson(getJSONFromQuery("weapons", "item_id", Integer.toString(itemID))); }

    public static BFWeapon getWeaponByItemName(String itemName) { return getWeaponFromJson(getJSONFromQuery("weapons", "item_name", itemName)); }

    // ########## WEAPON STATS ##########

    private static BFWeaponStats[] getWeaponStatsFromJson(JsonElement wStatsJson)
    {
        try
        {
            JsonArray object = wStatsJson.getAsJsonArray();

            BFWeaponStats[] stats = new BFWeaponStats[object.size()];
            for (int i = 0; i < stats.length; i++)
            {
                JsonObject element = object.get(i).getAsJsonObject();
                stats[i] = new BFWeaponStats(
                        element.get("id").getAsInt(),
                        element.get("match_id").getAsInt(),
                        element.get("player_id").getAsInt(),
                        element.get("weapon_id").getAsInt(),
                        element.get("shots_fired").getAsInt(),
                        element.get("shots_hit").getAsInt()
                );
            }
            return stats;
        }
        catch (IllegalStateException e)
        {
            return new BFWeaponStats[]{new BFWeaponStats(0, 0, 0, 0, 0, 0)};
        }
    }

    public static BFWeaponStats[] getWeaponStatsForPlayer(BFPlayer player, BFWeapon weapon)
    {
        RequestBuilder rb = new RequestBuilder("weapon_stats");
        rb.addSearchQuery("player_id", Integer.toString(player.getId()));
        rb.addSearchQuery("weapon_id", Integer.toString(weapon.getId()));
        return getWeaponStatsFromJson(getJSONFromRequestBuilder(rb));
    }

    public static BFWeaponStats[] getWeaponStatsFromBFPlayer(BFPlayer player) { return getWeaponStatsFromJson(getJSONFromQuery("weapon_stats", "player_id", Integer.toString(player.getId()))); }

    public static BFWeaponStats[] getWeaponStatsFromWeapon(BFWeapon weapon) { return getWeaponStatsFromJson(getJSONFromQuery("weapon_stats", "weapon_id", Integer.toString(weapon.getId()))); }

    public static BFWeaponStats[] getWeaponStatsFromWeapon(BFMatch match) { return getWeaponStatsFromJson(getJSONFromQuery("weapon_stats", "match_id", Integer.toString(match.getId()))); }

    // ########## KILLS ##########

    private static BFKill[] getKillObjFromJson(JsonElement killsJson)
    {
        try
        {
            JsonArray killsArray = killsJson.getAsJsonArray();
            BFKill[] kills = new BFKill[killsArray.size()];

            for (int i = 0; i < killsArray.size(); i++)
            {
                JsonObject element = killsArray.get(i).getAsJsonObject();
                kills[i] = new BFKill(
                        element.get("id").getAsInt(),
                        element.get("match_id").getAsInt(),
                        element.get("source_player").getAsInt(),
                        element.get("target_player").getAsInt(),
                        element.get("weapon").getAsInt(),
                        element.get("source_x").getAsDouble(),
                        element.get("source_y").getAsDouble(),
                        element.get("source_z").getAsDouble(),
                        element.get("target_x").getAsDouble(),
                        element.get("target_y").getAsDouble(),
                        element.get("target_z").getAsDouble()
                );
            }

            return kills;
        }
        catch (IllegalStateException e)
        {
            return new BFKill[0];
        }
    }

    public static BFKill[] getKillsBySourceID(int sourceID) { return getKillObjFromJson(getJSONFromQuery("match_kills", "source_player", Integer.toString(sourceID))); }

    public static BFKill[] getKillsByTargetID(int targetID) { return getKillObjFromJson(getJSONFromQuery("match_kills", "target_player", Integer.toString(targetID))); }

    public static BFKill[] getKillsByBFPlayerSource(BFPlayer sourcePlayer) { return getKillsBySourceID(sourcePlayer.getId()); }

    public static BFKill[] getKillsByBFPlayerTarget(BFPlayer targetPlayer) { return getKillsBySourceID(targetPlayer.getId()); }

    public static BFKill[] getAllKills() { return getKillObjFromJson(getJSONFromTable("match_kills")); }

    // ########## TOTALS ##########

    private static int getKillsFromJson(JsonElement killsJson)
    {
        try
        {
            return killsJson.getAsJsonArray().get(0).getAsJsonObject().get("kills").getAsInt();
        }
        catch (IllegalStateException e)
        {
            return 0;
        }
    }

    public static int getKillTotalFromPlayer(BFPlayer player) { return getKillsFromJson(getJSONFromQuery("kills", "uuid", player.getUUID())); }

    private static int getWinsFromJson(JsonElement winsJson)
    {
        try
        {
            return winsJson.getAsJsonArray().get(0).getAsJsonObject().get("wins").getAsInt();
        }
        catch (IllegalStateException e)
        {
            return 0;
        }
    }

    public static int getWinsTotalFromPlayer(BFPlayer player) { return getWinsFromJson(getJSONFromQuery("wins", "uuid", player.getUUID())); }

    public static java.util.Map<String, Integer> getWinMap()
    {
        try
        {
            JsonArray object = getJSONFromTable("wins").getAsJsonArray();

            java.util.Map<String, Integer> winsMap = new HashMap<>();

            for (JsonElement element : object)
            {
                winsMap.put(element.getAsJsonObject().get("uuid").getAsString(), element.getAsJsonObject().get("wins").getAsInt());
            }

            return winsMap;
        }
        catch (IllegalStateException e)
        {
            return new HashMap<>();
        }
    }

    public static java.util.Map<String, Integer> getKillMap()
    {
        try
        {
            JsonArray object = getJSONFromTable("kills").getAsJsonArray();

            java.util.Map<String, Integer> winsMap = new HashMap<>();

            for (JsonElement element : object)
            {
                winsMap.put(element.getAsJsonObject().get("uuid").getAsString(), element.getAsJsonObject().get("kills").getAsInt());
            }

            return winsMap;
        }
        catch (IllegalStateException e)
        {
            return new HashMap<>();
        }
    }

    // ########## PLAYERS ##########

    private static BFPlayer getPlayerFromJson(JsonElement playerJson)
    {
        try
        {
            JsonObject player = playerJson.getAsJsonArray().get(0).getAsJsonObject();
            return new BFPlayer(player.get("id").getAsInt(), player.get("uuid").getAsString(), player.get("username").getAsString(), player.get("last_seen").getAsString());
        }
        catch (IllegalStateException e)
        {
            return new BFPlayer(0, "", "", "");
        }
    }

    private static BFPlayer[] getPlayersFromJson(JsonElement playerJson)
    {
        try
        {
            JsonArray json = playerJson.getAsJsonArray();
            BFPlayer[] players = new BFPlayer[json.size()];

            for (int i = 0; i < json.size(); i++)
            {
                JsonObject object = json.get(i).getAsJsonObject();
                players[i] = new BFPlayer(object.get("id").getAsInt(), object.get("uuid").getAsString(), object.get("username").getAsString(), object.get("last_seen").getAsString());
            }

            return players;
        }
        catch (IllegalStateException e)
        {
            return new BFPlayer[0];
        }
    }

    public static BFPlayer getPlayerByID(int ID) { return getPlayerFromJson(getJSONFromQuery("players", "id", Integer.toString(ID))); }

    public static BFPlayer getPlayerByUUID(String uuid)
    {
        return getPlayerFromJson(getJSONFromQuery("players", "uuid", uuid.replaceAll("-", "")));
    }

    public static BFPlayer getPlayerByUsername(String username) { return getPlayerFromJson(getJSONFromQuery("players", "username", username)); }

    public static BFPlayer[] getAllPlayers() { return getPlayersFromJson(getJSONFromTable("players")); }

    // ########## DISCORD ##########

    private static String getDiscordFromJson(JsonElement discordJson)
    {
        try
        {
            JsonObject discord = discordJson.getAsJsonArray().get(0).getAsJsonObject();
            return discord.get("discord_id").getAsString();
        }
        catch (IllegalStateException e)
        {
            return "";
        }
    }

    private static String getUUIDFromJson(JsonElement discordJson)
    {
        try
        {
            JsonObject discord = discordJson.getAsJsonArray().get(0).getAsJsonObject();
            return discord.get("uuid").getAsString();
        }
        catch (IllegalStateException e)
        {
            return "";
        }
    }

    public static String getDiscordByPlayer(BFPlayer player) { return getDiscordFromJson(getJSONFromQuery("linked_discord", "uuid", player.getUUID())); }

    public static String getDiscordByUUID(String uuid) { return getDiscordFromJson(getJSONFromQuery("linked_discord", "uuid", uuid)); }

    public static String getUUIDByDiscord(String discordID) { return getUUIDFromJson((getJSONFromQuery("linked_discord", "discord_id", discordID))); }

    // ########## MATCHES ##########

    private static BFMatch[] getMatchesFromJson(JsonElement matchArrayJson)
    {
        try
        {
            JsonArray jsonObject = matchArrayJson.getAsJsonArray();
            BFMatch[] matches = new BFMatch[jsonObject.size()];

            for (int i = 0; i < matches.length; i++)
            {
                JsonObject element = jsonObject.get(i).getAsJsonObject();
                matches[i] = new BFMatch(
                        element.get("id").getAsInt(),
                        element.get("number").getAsInt(),
                        element.get("start_date").getAsString(),
                        element.get("end_date").getAsString(),
                        element.get("winning_player_id").getAsInt()
                );
            }
            return matches;
        }
        catch (IllegalStateException e)
        {
            return new BFMatch[]{new BFMatch(0, 0, "", "", 0)};
        }
    }

    public static BFMatch getMatchFromID(int ID) { return getMatchesFromJson(getJSONFromQuery("matches", "id", Integer.toString(ID)))[0]; }

    public static BFMatch getMatchFromNumber(int number) { return getMatchesFromJson(getJSONFromQuery("matches", "number", Integer.toString(number)))[0]; }

    public static BFMatch[] getMatchesFromWinningPlayer(BFPlayer player) { return getMatchesFromJson(getJSONFromQuery("matches", "winning_player_id", Integer.toString(player.getId()))); }

    public static BFMatch[] getAllMatches() { return getMatchesFromJson(getJSONFromTable("matches")); }

    // ########## PARTICIPANT MATCHES ##########

    private static int[] getPMatchesFromJson(JsonElement pMatchArrayJson)
    {
        try
        {
            JsonArray object = pMatchArrayJson.getAsJsonArray();

            int[] matches = new int[object.size()];
            for (int i = 0; i < matches.length; i++)
            {
                matches[i] = object.get(i).getAsJsonObject().get("match_id").getAsInt();
            }

            return matches;
        }
        catch (IllegalStateException e)
        {
            return new int[0];
        }
    }

    private static int[] getMPlayersFromJson(JsonElement mPlayerArrayJson)
    {
        try
        {
            JsonArray object = mPlayerArrayJson.getAsJsonArray();

            int[] players = new int[object.size()];
            for (int i = 0; i < players.length; i++)
            {
                players[i] = object.get(i).getAsJsonObject().get("player_id").getAsInt();
            }
            return players;
        }
        catch (IllegalStateException e)
        {
            return new int[0];
        }
    }

    public static int[] getMatchesContainingPlayer(BFPlayer player) { return getPMatchesFromJson(getJSONFromQuery("match_participants", "player_id", Integer.toString(player.getId()))); }

    public static int[] getPlayersInMatch(BFMatch match) { return getMPlayersFromJson(getJSONFromQuery("match_participants", "match_id", Integer.toString(match.getId()))); }

    // ########## ACCESSORIES ##########

    private static BFAccessory[] getAccessoriesFromJson(JsonElement accessoryJson)
    {
        try
        {
            JsonArray object = accessoryJson.getAsJsonArray();

            BFAccessory[] accessories = new BFAccessory[object.size()];
            for (int i = 0; i < accessories.length; i++)
            {
                JsonObject element = object.get(i).getAsJsonObject();
                accessories[i] = new BFAccessory(
                        element.get("id").getAsInt(),
                        element.get("accessory_type").getAsInt(),
                        element.get("name").getAsString(),
                        element.get("data").getAsString(),
                        element.get("enabled").getAsInt() == 1,
                        element.get("hidden").getAsInt() == 1);
            }

            return accessories;
        }
        catch (IllegalStateException e)
        {
            return new BFAccessory[]{new BFAccessory(0, 0, "", "", false, false)};
        }
    }

    public static BFAccessory getAccessoryFromID(int ID) { return getAccessoriesFromJson(getJSONFromQuery("accessories", "id", Integer.toString(ID)))[0]; }

    public static BFAccessory[] getAccessoryFromTypeID(int typeID) { return getAccessoriesFromJson(getJSONFromQuery("accessories", "accessory_type", Integer.toString(typeID))); }

    public static BFAccessory getAccessoryFromName(String name) { return getAccessoriesFromJson(getJSONFromQuery("accessories", "name", name))[0]; }

    public static BFAccessory getAccessoryFromData(String data) { return getAccessoriesFromJson(getJSONFromQuery("accessories", "data", data))[0]; }

    public static BFAccessory[] getAccessoriesIfEnabled(boolean enabled) { return getAccessoriesFromJson(getJSONFromQuery("accessories", "enabled", Integer.toString(enabled ? 0 : 1))); }

    public static BFAccessory[] getAccessoriesIfHidden(boolean hidden) { return getAccessoriesFromJson(getJSONFromQuery("accessories", "hidden", Integer.toString(hidden ? 1 : 0))); }

    // ########## PLAYER ACCESSORIES ##########

    private static int[] getAccessoryIDsFromJson(JsonElement pAccessoryJson)
    {
        try
        {
            JsonArray object = pAccessoryJson.getAsJsonArray();

            int[] pAccessories = new int[object.size()];
            for (int i = 0; i < pAccessories.length; i++)
            {
                JsonObject element = object.get(i).getAsJsonObject();
                pAccessories[i] = element.get("accessory_id").getAsInt();
            }

            return pAccessories;
        }
        catch (IllegalStateException e)
        {
            return new int[0];
        }
    }

    public static int[] getOwnedAccessoriesFromUUID(String UUID) { return getAccessoryIDsFromJson(getJSONFromQuery("owned_accessories", "uuid", UUID.replaceAll("-", ""))); }

    public static int[] getOwnedAccessoriesFromBFPlayer(BFPlayer player) { return getAccessoryIDsFromJson(getJSONFromQuery("owned_accessories", "uuid", player.getUUID().replaceAll("-", ""))); }

    // ########## EXTRA ##########

    public static BFKill[] getKillsOnPlayer(BFPlayer source, BFPlayer target)
    {
        try
        {
            return getKillObjFromJson(getJSONFromRequestBuilder(new RequestBuilder("match_kills")
                    .addSearchQuery("source_player", Integer.toString(source.getId()))
                    .addSearchQuery("target_player", Integer.toString(target.getId())))
                    .getAsJsonArray());
        }
        catch (IllegalStateException e)
        {
            return new BFKill[0];
        }
    }

    public static java.util.Map<BFPlayer, Integer> getScoreMap()
    {
        BFPlayer[] players = getAllPlayers();
        java.util.Map<String, Integer> winMap = getWinMap();
        java.util.Map<String, Integer> killMap = getKillMap();
        java.util.Map<BFPlayer, Integer> scoreMap = new HashMap<>();

        for (BFPlayer player : players)
        {
            int w = winMap.getOrDefault(player.getUUID(), 0);
            int k = killMap.getOrDefault(player.getUUID(), 0);

            int score = (k + (w * 5));

            scoreMap.put(player, score);
        }

        return scoreMap;
    }

}
