package io.github.tastac.bfj.components;

import io.github.tastac.bfj.DataRetriever;

import java.util.Date;

public class BFPlayer {

    private int ID;
    private String uuid;
    private String username;
    private String lastSeen;

    private int matchesID[];
    private BFMatch[] matches;
    private BFKill kill[];
    private int wins = -1;

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

    public int[] getMatchIDs(){
        if(matchesID == null) matchesID = DataRetriever.getMatchesContainingPlayer(this);
        return matchesID;
    }

    public BFMatch[] getMatches(){
        if(matchesID == null) DataRetriever.getMatchesContainingPlayer(this);
        if(matches == null){
            matches = new BFMatch[matchesID.length];
            for(int i : matchesID){
                matches[i] = DataRetriever.getMatchFromID(i);
            }
        }
        return matches;
    }

    public BFKill[] getKills(){
        if (kill == null) kill = DataRetriever.getKillsBySourceID(ID);
            return kill;
    }

    public int getTotalKills(){
        if (kill == null) kill = DataRetriever.getKillsBySourceID(ID);
        return kill.length;
    }

    public int getTotalWins(){
        if (wins == -1) wins = DataRetriever.getMatchesFromWinningPlayer(this).length;
        return wins;
    }
}
