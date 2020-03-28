package io.github.tastac.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.javafx.geom.Vec3d;
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

public class BFDataRetriever {

    private static final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static final String apiURL = "https://api.battlefieldsmc.net/api/?type=";

    private static JsonElement getJSONFromTable(String table){ return requestFromURL(apiURL + table); }
    private static JsonElement getJSONFromQuery(String table, String dataID, String query){ return requestFromURL(apiURL + table + "&" + dataID + "=" + query); }

    private static JsonElement requestFromURL(String url){
        HttpGet request = new HttpGet(url);

        request.addHeader(HttpHeaders.USER_AGENT, "BFHeatmapper");

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

    // ########## WEAPONS ##########

    public static BFWeapon getWeaponByID(int ID){
        JsonObject weaponJson = getJSONFromQuery("cubg_weapons", "id", Integer.toString(ID)).getAsJsonArray().get(0).getAsJsonObject();
        return new BFWeapon(weaponJson.get("id").getAsInt(), weaponJson.get("item_id").getAsInt(), weaponJson.get("item_name").getAsString());
    }

    public static BFWeapon getWeaponByItemID(int itemID){
        JsonObject weaponJson = getJSONFromQuery("cubg_weapons", "item_id", Integer.toString(itemID)).getAsJsonArray().get(0).getAsJsonObject();
        return new BFWeapon(weaponJson.get("id").getAsInt(), weaponJson.get("item_id").getAsInt(), weaponJson.get("item_name").getAsString());
    }

    public static BFWeapon getWeaponByItemName(String itemName){
        JsonObject weaponJson = getJSONFromQuery("cubg_weapons", "item_name", itemName).getAsJsonArray().get(0).getAsJsonObject();
        return new BFWeapon(weaponJson.get("id").getAsInt(), weaponJson.get("item_id").getAsInt(), weaponJson.get("item_name").getAsString());
    }

    // ########## KILLS ##########

    private static BFKill[] getKillsFromJson(JsonArray killsJson){
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

    public static BFPlayer getPlayerByID(int ID){
        JsonObject playerJson = getJSONFromQuery("cubg_players", "id", Integer.toString(ID)).getAsJsonArray().get(0).getAsJsonObject();
        try{
            return new BFPlayer(playerJson.get("id").getAsInt(), playerJson.get("uuid").getAsString(), playerJson.get("username").getAsString(), new SimpleDateFormat("yyyy-mm-dd").parse(playerJson.get("last_seen").getAsString()));
        }catch (ParseException e){ e.printStackTrace(); }
        return new BFPlayer(playerJson.get("id").getAsInt(), playerJson.get("uuid").getAsString(), playerJson.get("username").getAsString(), null);
    }

}
