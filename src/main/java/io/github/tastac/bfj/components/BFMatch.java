package io.github.tastac.bfj.components;

import io.github.tastac.bfj.DataRetriever;

import java.util.Objects;

public class BFMatch {

    private int ID;
    private int number;
    private String startDate;
    private String endDate;
    private int winningPlayerID;

    private int[] participatingPlayerIDs;
    private BFPlayer[] participatingPlayers;

    public BFMatch(int ID, int number, String startDate, String endDate, int winningPlayerID){
        this.ID = ID;
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
        this.winningPlayerID = winningPlayerID;
    }

    //TODO add documentation to all these methods

    public int getID() { return ID; }

    public int getNumber() { return number; }

    public String getStartDate() { return startDate; }

    public String getEndDate() { return endDate; }

    public BFPlayer getWinningPlayer() { return DataRetriever.getPlayerByID(winningPlayerID); }

    public int[] getParticipatingPlayerIDs(){
        if(participatingPlayerIDs == null) DataRetriever.getPlayersInMatch(this);
        return participatingPlayerIDs;
    }

    public BFPlayer[] getParticipatingPlayers(){
        if(participatingPlayerIDs == null) DataRetriever.getPlayersInMatch(this);
        if(participatingPlayers == null){
            participatingPlayers = new BFPlayer[participatingPlayerIDs.length];
            for(int i : participatingPlayerIDs){
                participatingPlayers[i] = DataRetriever.getPlayerByID(i);
            }
        }
        return participatingPlayers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BFMatch)) return false;
        BFMatch bfMatch = (BFMatch) o;
        return ID == bfMatch.ID &&
                number == bfMatch.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, number);
    }
}
