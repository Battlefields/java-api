package io.github.tastac.bfj.api.components;

import io.github.tastac.bfj.api.DataRetriever;

import java.util.Date;

public class BFPlayer {

    private int ID;
    private String uuid;
    private String username;
    private Date lastSeen;

    private BFMatch matches[];
    private BFKill kill[];
    private int wins = -1;

    public BFPlayer(int ID, String uuid, String username, Date lastSeen){
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

    public Date getLastSeen() {
        return lastSeen;
    }

    public BFMatch[] getMatches(){
        if(matches == null) matches = DataRetriever.getMatchesContainingPlayer(this);
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
