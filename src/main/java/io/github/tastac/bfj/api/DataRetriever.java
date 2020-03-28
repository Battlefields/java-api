package io.github.tastac.bfj.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.javafx.geom.Vec3d;
import io.github.tastac.bfj.api.components.BFKill;
import io.github.tastac.bfj.api.components.BFMatch;
import io.github.tastac.bfj.api.components.BFPlayer;
import io.github.tastac.bfj.api.components.BFWeapon;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataRetriever {

    private static final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static final String apiURL = "https://api.battlefieldsmc.net/api/?type=";
    private static final DateTimeFormatter matchDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
                return JsonParser.parseString(EntityUtils.toString(entity)).getAsJsonObject().get("detail");
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    //TODO Add documentation to all of these methods

    // ########## WEAPONS ##########

    private static BFWeapon getWeaponFromJson(JsonObject weaponJson){
        return new BFWeapon(
                weaponJson.get("id").getAsInt(),
                weaponJson.get("item_id").getAsInt(),
                weaponJson.get("item_name").getAsString());
    }

    public static BFWeapon getWeaponByID(int ID){ return getWeaponFromJson(getJSONFromQuery("cubg_weapons", "id", Integer.toString(ID)).getAsJsonArray().get(0).getAsJsonObject()); }

    public static BFWeapon getWeaponByItemID(int itemID){ return getWeaponFromJson(getJSONFromQuery("cubg_weapons", "item_id", Integer.toString(itemID)).getAsJsonArray().get(0).getAsJsonObject()); }

    public static BFWeapon getWeaponByItemName(String itemName){ return getWeaponFromJson(getJSONFromQuery("cubg_weapons", "item_name", itemName).getAsJsonArray().get(0).getAsJsonObject()); }

    // ########## KILLS ##########

    public static BFKill[] getKillsFromJson(JsonArray killsJson){
        BFKill[] kills = new BFKill[killsJson.size()];

        for(int i = 0; i < killsJson.size(); i++){
            JsonObject element = killsJson.get(i).getAsJsonObject();
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
    }

    public static BFKill[] getKillsBySourceID(int sourceID){ return getKillsFromJson(getJSONFromQuery("cubg_kills", "source_player", Integer.toString(sourceID)).getAsJsonArray()); }

    public static BFKill[] getKillsByTargetID(int targetID){ return getKillsFromJson(getJSONFromQuery("cubg_kills", "target_player", Integer.toString(targetID)).getAsJsonArray()); }

    public static BFKill[] getKillsByBFPlayerSource(BFPlayer sourcePlayer){ return getKillsBySourceID(sourcePlayer.getID()); }

    public static BFKill[] getKillsByBFPlayerTarget(BFPlayer targetPlayer){ return getKillsBySourceID(targetPlayer.getID()); }

    // ########## PLAYERS ##########

    public static BFPlayer getPlayerFromJson(JsonObject playerJson){
        try{
            return new BFPlayer(playerJson.get("id").getAsInt(), playerJson.get("uuid").getAsString(), playerJson.get("username").getAsString(), new SimpleDateFormat("yyyy-mm-dd").parse(playerJson.get("last_seen").getAsString()));
        }catch (ParseException e){ e.printStackTrace(); }
        return new BFPlayer(playerJson.get("id").getAsInt(), playerJson.get("uuid").getAsString(), playerJson.get("username").getAsString(), null);
    }

    public static BFPlayer getPlayerByID(int ID){ return getPlayerFromJson(getJSONFromQuery("cubg_players", "id", Integer.toString(ID)).getAsJsonArray().get(0).getAsJsonObject()); }

    public static BFPlayer getPlayerByUUID(String uuid){ return getPlayerFromJson(getJSONFromQuery("cubg_players", "uuid", uuid).getAsJsonArray().get(0).getAsJsonObject()); }

    public static BFPlayer getPlayerByUsername(String username){ return getPlayerFromJson(getJSONFromQuery("cubg_players", "username", username).getAsJsonArray().get(0).getAsJsonObject()); }

    // ########## MATCHES ##########

    public static BFMatch[] getMatchesFromJson(JsonArray matchArrayJson){
        BFMatch[] matches = new BFMatch[matchArrayJson.size()];
        for(int i = 0; i < matches.length; i++){
            JsonObject element = matchArrayJson.get(i).getAsJsonObject();
            matches[i] = new BFMatch(
                    element.get("id").getAsInt(),
                    element.get("number").getAsInt(),
                    LocalDateTime.parse(element.get("start_date").getAsString(), matchDateFormatter),
                    LocalDateTime.parse(element.get("end_date").getAsString(), matchDateFormatter),
                    element.get("winning_player_id").getAsInt()
            );
        }
        return matches;
    }

    public static BFMatch getMatchFromID(int ID){ return getMatchesFromJson(getJSONFromQuery("cubg_match", "id", Integer.toString(ID)).getAsJsonArray())[0]; }

    public static BFMatch getMatchFromNumber(int number){ return getMatchesFromJson(getJSONFromQuery("cubg_match", "number", Integer.toString(number)).getAsJsonArray())[0]; }

    public static BFMatch[] getMatchesFromWinningPlayer(BFPlayer player){ return getMatchesFromJson(getJSONFromQuery("cubg_match", "winning_player_id", Integer.toString(player.getID())).getAsJsonArray()); }

    // ########## PARTICIPANT MATCHES ##########

    public static BFMatch[] getPMatchesFromJson(JsonArray pMatchArrayJson){
        BFMatch[] matches = new BFMatch[pMatchArrayJson.size()];
        for(int i = 0; i < matches.length; i++){
            matches[i] = getMatchFromID(pMatchArrayJson.get(i).getAsJsonObject().get("match_id").getAsInt());
        }
        return matches;
    }

    private static BFPlayer[] getMPlayersFromJson(JsonArray mPlayerArrayJson){
        BFPlayer[] players = new BFPlayer[mPlayerArrayJson.size()];
        for(int i = 0; i < players.length; i++){
            players[i] = getPlayerByID(mPlayerArrayJson.get(i).getAsJsonObject().get("player_id").getAsInt());
        }
        return players;
    }

    public static BFMatch[] getMatchesContainingPlayer(BFPlayer player){ return getMatchesFromJson(getJSONFromQuery("cubg_participants", "player_id", Integer.toString(player.getID())).getAsJsonArray()); }

    public static BFMatch[] getPlayersInMatch(BFMatch match){ return getMatchesFromJson(getJSONFromQuery("cubg_participants", "match_id", Integer.toString(match.getID())).getAsJsonArray()); }

    // ########## EXTRA ##########

    public static BFKill[] getKillsOnPlayer(BFPlayer source, BFPlayer target){
        return getKillsFromJson(getJSONFromRequestBuilder(new RequestBuilder("cubg_kills")
                        .addSearchQuery("source_player", Integer.toString(source.getID()))
                        .addSearchQuery("target_player", Integer.toString(target.getID())))
                .getAsJsonArray());
    }

}
