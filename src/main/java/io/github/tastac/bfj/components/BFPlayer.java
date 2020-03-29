package io.github.tastac.bfj.components;

import io.github.tastac.bfj.DataRetriever;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BFPlayer {

    private int ID;
    private String uuid;
    private String username;
    private String lastSeen;

    public BFPlayer(int ID, String uuid, String username, String lastSeen){
        this.ID = ID;
        this.uuid = uuid;
        this.username = username;
        this.lastSeen = lastSeen;
    }

    //TODO add documentation to all these methods

    public int getID() {
        return ID;
    }

    public String getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public int[] getMatchIDs(){ return DataRetriever.getMatchesContainingPlayer(this); }

    public BFKill[] getKills(){ return DataRetriever.getKillsByBFPlayerSource(this); }

    public int getTotalKills(){ return DataRetriever.getKillTotalFromPlayer(this); }

    public int getTotalWins(){ return DataRetriever.getWinsTotalFromPlayer(this); }

    public int getTotalDeaths(){ return getMatchIDs().length - getTotalWins(); }

    public int getRank(){
        java.util.Map<BFPlayer, Integer> scoreMap = DataRetriever.getScoreMap();
        List<BFPlayer> list = new ArrayList<>(scoreMap.keySet());
        Collections.sort(list, (a, b) -> scoreMap.get(b) - scoreMap.get(a));

        return list.indexOf(this) + 1;
    }

    public float getKDRatio(){
        int kills = getTotalKills();
        int deaths = getMatchIDs().length - getTotalWins();

        if(deaths <= 0){
            return 0f;
        }else{
            return kills / deaths;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BFPlayer)) return false;
        BFPlayer bfPlayer = (BFPlayer) o;
        return ID == bfPlayer.ID &&
                uuid.equals(bfPlayer.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, uuid);
    }
}
