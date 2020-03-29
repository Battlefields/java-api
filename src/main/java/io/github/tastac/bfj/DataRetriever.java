package io.github.tastac.bfj;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.javafx.geom.Vec3d;
import io.github.tastac.bfj.components.BFKill;
import io.github.tastac.bfj.components.BFMatch;
import io.github.tastac.bfj.components.BFPlayer;
import io.github.tastac.bfj.components.BFWeapon;
import javafx.util.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataRetriever {

    private static final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static final String apiURL = "https://api.battlefieldsmc.net/api/?type=";
    private static final DateTimeFormatter matchDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //TODO add a map of previously retrieved players for quick access later (a cache) include expire timer

    public static JsonElement getJSONFromRequestBuilder(RequestBuilder requestBuilder){ return requestFromURL(apiURL + requestBuilder.getString()); }
    public static JsonElement getJSONFromQuery(String table, String dataID, String query){ return requestFromURL(apiURL + table + "&" + dataID + "=" + query); }
    public static JsonElement getJSONFromTable(String table){ return requestFromURL(apiURL + table); }

    private static JsonElement requestFromURL(String url){
        HttpGet request = new HttpGet(url);

        request.addHeader(HttpHeaders.USER_AGENT, "BFJ");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                JsonElement object = JsonParser.parseString(EntityUtils.toString(entity)).getAsJsonObject().get("detail");
                return object;
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    //TODO Add documentation to all of these methods

    // ########## WEAPONS ##########

    private static BFWeapon getWeaponFromJson(JsonElement weaponJson){
        try {
            JsonObject weapon = weaponJson.getAsJsonArray().get(0).getAsJsonObject();

            return new BFWeapon(
                    weapon.get("id").getAsInt(),
                    weapon.get("item_id").getAsInt(),
                    weapon.get("item_name").getAsString());
        }catch(IllegalStateException e){
            return new BFWeapon(0, 0, "");
        }
    }

    public static BFWeapon getWeaponByID(int ID){ return getWeaponFromJson(getJSONFromQuery("cubg_weapons", "id", Integer.toString(ID))); }

    public static BFWeapon getWeaponByItemID(int itemID){ return getWeaponFromJson(getJSONFromQuery("cubg_weapons", "item_id", Integer.toString(itemID))); }

    public static BFWeapon getWeaponByItemName(String itemName){ return getWeaponFromJson(getJSONFromQuery("cubg_weapons", "item_name", itemName)); }

    // ########## KILLS OBJECTS ##########

    public static BFKill[] getKillObjFromJson(JsonElement killsJson){
        try{
            JsonArray killsArray = killsJson.getAsJsonArray();
            BFKill[] kills = new BFKill[killsArray.size()];

            for(int i = 0; i < killsArray.size(); i++){
                JsonObject element = killsArray.get(i).getAsJsonObject();
                kills[i] = new BFKill(
                        element.get("id").getAsInt(),
                        element.get("match_id").getAsInt(),
                        element.get("source_player").getAsInt(),
                        element.get("target_player").getAsInt(),
                        new Vec3d(element.get("source_x").getAsDouble(), element.get("source_y").getAsDouble(), element.get("source_z").getAsDouble()),
                        new Vec3d(element.get("target_x").getAsDouble(), element.get("target_y").getAsDouble(), element.get("target_z").getAsDouble()),
                        element.get("weapon").getAsInt()
                );
            }

            return kills;
        }catch (IllegalStateException e){
            return new BFKill[0];
        }
    }

    public static BFKill[] getKillsBySourceID(int sourceID){ return getKillObjFromJson(getJSONFromQuery("cubg_kills", "source_player", Integer.toString(sourceID))); }

    public static BFKill[] getKillsByTargetID(int targetID){ return getKillObjFromJson(getJSONFromQuery("cubg_kills", "target_player", Integer.toString(targetID))); }

    public static BFKill[] getKillsByBFPlayerSource(BFPlayer sourcePlayer){ return getKillsBySourceID(sourcePlayer.getID()); }

    public static BFKill[] getKillsByBFPlayerTarget(BFPlayer targetPlayer){ return getKillsBySourceID(targetPlayer.getID()); }

    // ########## TOTALS ##########

    public static int getKillsFromJson(JsonElement killsJson){
        try{
            return killsJson.getAsJsonArray().get(0).getAsJsonObject().get("kills").getAsInt();
        }catch (IllegalStateException e){
            return 0;
        }
    }

    public static int getKillTotalFromPlayer(BFPlayer player){ return getKillsFromJson(getJSONFromQuery("cubg_most_kills", "uuid", player.getUUID())); }

    public static int getWinsFromJson(JsonElement winsJson){
        try{
            return winsJson.getAsJsonArray().get(0).getAsJsonObject().get("wins").getAsInt();
        }catch (IllegalStateException e){
            return 0;
        }
    }

    public static int getWinsTotalFromPlayer(BFPlayer player){ return getKillsFromJson(getJSONFromQuery("cubg_most_wins", "uuid", player.getUUID())); }

    public static java.util.Map<String, Integer> getWinMap(){
        try{
            JsonArray object = getJSONFromTable("cubg_most_wins").getAsJsonArray();

            java.util.Map<String, Integer> winsMap = new HashMap<>();

            for(JsonElement element : object){
                winsMap.put(element.getAsJsonObject().get("uuid").getAsString(), element.getAsJsonObject().get("wins").getAsInt());
            }

            return winsMap;
        }catch(IllegalStateException e){
            return new HashMap<>();
        }
    }

    public static java.util.Map<String, Integer> getKillMap(){
        try{
            JsonArray object = getJSONFromTable("cubg_most_kills").getAsJsonArray();

            java.util.Map<String, Integer> winsMap = new HashMap<>();

            for(JsonElement element : object){
                winsMap.put(element.getAsJsonObject().get("uuid").getAsString(), element.getAsJsonObject().get("kills").getAsInt());
            }

            return winsMap;
        }catch(IllegalStateException e){
            return new HashMap<>();
        }
    }

    // ########## PLAYERS ##########

    public static BFPlayer getPlayerFromJson(JsonElement playerJson){
        try{
            JsonObject player = playerJson.getAsJsonArray().get(0).getAsJsonObject();
            return new BFPlayer(player.get("id").getAsInt(), player.get("uuid").getAsString(), player.get("username").getAsString(), player.get("last_seen").getAsString());
        }catch (IllegalStateException e){
            return new BFPlayer(0, "", "", "");
        }
    }

    public static BFPlayer[] getPlayersFromJson(JsonElement playerJson){
        try{
            JsonArray json = playerJson.getAsJsonArray();
            BFPlayer[] players = new BFPlayer[json.size()];

            for(int i = 0; i < json.size(); i++){
                JsonObject object = json.get(i).getAsJsonObject();
                players[i] = new BFPlayer(object.get("id").getAsInt(), object.get("uuid").getAsString(), object.get("username").getAsString(), object.get("last_seen").getAsString());
            }

            return players;
        }catch (IllegalStateException e){
            return new BFPlayer[0];
        }
    }

    public static BFPlayer getPlayerByID(int ID){ return getPlayerFromJson(getJSONFromQuery("cubg_players", "id", Integer.toString(ID))); }

    public static BFPlayer getPlayerByUUID(String uuid){
        uuid.replaceAll("-", "");
        return getPlayerFromJson(getJSONFromQuery("cubg_players", "uuid", uuid));
    }

    public static BFPlayer getPlayerByUsername(String username){ return getPlayerFromJson(getJSONFromQuery("cubg_players", "username", username)); }

    public static BFPlayer[] getAllPlayers(){ return getPlayersFromJson(getJSONFromTable("cubg_players")); }

    // ########## MATCHES ##########

    public static BFMatch[] getMatchesFromJson(JsonElement matchArrayJson){
        try {
            JsonArray jsonObject = matchArrayJson.getAsJsonArray();
            BFMatch[] matches = new BFMatch[jsonObject.size()];

            for (int i = 0; i < matches.length; i++) {
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
        }catch (IllegalStateException e){
            return new BFMatch[0];
        }
    }

    public static BFMatch getMatchFromID(int ID){
        BFMatch[] matches = getMatchesFromJson(getJSONFromQuery("cubg_match", "id", Integer.toString(ID)));
        return (matches.length == 0 ? new BFMatch(0, 0, "", "", 0) : matches[1]);
    }

    public static BFMatch getMatchFromNumber(int number){
        BFMatch[] matches = getMatchesFromJson(getJSONFromQuery("cubg_match", "number", Integer.toString(number)));
        return (matches.length == 0 ? new BFMatch(0, 0, "", "", 0) : matches[1]);
    }

    public static BFMatch[] getMatchesFromWinningPlayer(BFPlayer player){ return getMatchesFromJson(getJSONFromQuery("cubg_match", "winning_player_id", Integer.toString(player.getID()))); }

    // ########## PARTICIPANT MATCHES ##########

    public static int[] getPMatchesFromJson(JsonElement pMatchArrayJson){
        try{
            JsonArray object = pMatchArrayJson.getAsJsonArray();

            int[] matches = new int[object.size()];
            for(int i = 0; i < matches.length; i++){
                matches[i] = object.get(i).getAsJsonObject().get("match_id").getAsInt();
            }

            return matches;
        }catch(IllegalStateException e){
            return new int[0];
        }
    }

    private static int[] getMPlayersFromJson(JsonElement mPlayerArrayJson){
        try{
            JsonArray object = mPlayerArrayJson.getAsJsonArray();

            int[] players = new int[object.size()];
            for(int i = 0; i < players.length; i++){
                players[i] = object.get(i).getAsJsonObject().get("player_id").getAsInt();
            }
            return players;
        }catch(IllegalStateException e){
            return new int[0];
        }
    }

    public static int[] getMatchesContainingPlayer(BFPlayer player){ return getPMatchesFromJson(getJSONFromQuery("cubg_participants", "player_id", Integer.toString(player.getID()))); }

    public static int[] getPlayersInMatch(BFMatch match){ return getMPlayersFromJson(getJSONFromQuery("cubg_participants", "match_id", Integer.toString(match.getID()))); }

    // ########## EXTRA ##########

    public static BFKill[] getKillsOnPlayer(BFPlayer source, BFPlayer target){
        try {
            return getKillObjFromJson(getJSONFromRequestBuilder(new RequestBuilder("cubg_kills")
                    .addSearchQuery("source_player", Integer.toString(source.getID()))
                    .addSearchQuery("target_player", Integer.toString(target.getID())))
                    .getAsJsonArray());
        }catch(IllegalStateException e){
            return new BFKill[0];
        }
    }

    public static java.util.Map<BFPlayer, Integer> getScoreMap(){
        BFPlayer[] players = getAllPlayers();
        java.util.Map<String, Integer> winMap = getWinMap();
        java.util.Map<String, Integer> killMap = getKillMap();
        java.util.Map<BFPlayer, Integer> scoreMap = new HashMap<>();

        for(BFPlayer player : players){
            int w = winMap.getOrDefault(player.getUUID(), 0);
            int k = killMap.getOrDefault(player.getUUID(), 0);

            int score = (k + (w * 5));

            scoreMap.put(player, score);
        }

        return scoreMap;
    }

}