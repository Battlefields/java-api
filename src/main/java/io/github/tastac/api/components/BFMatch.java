package io.github.tastac.api.components;

import io.github.tastac.api.DataRetriever;

import java.time.LocalDateTime;

public class BFMatch {

    private int ID;
    private int number;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int winningPlayerID;

    private int[] participatingPlayers;

    public BFMatch(int ID, int number, LocalDateTime startDate, LocalDateTime endDate, int winningPlayerID){
        this.ID = ID;
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
        this.winningPlayerID = winningPlayerID;
    }

    //TODO add documentation to all these methods

    public int getID() { return ID; }

    public int getNumber() { return number; }

    public LocalDateTime getStartDate() { return startDate; }

    public LocalDateTime getEndDate() { return endDate; }

    public BFPlayer getWinningPlayer() { return DataRetriever.getPlayerByID(winningPlayerID); }

    public int[] getParticipatingPlayers(){
        if(participatingPlayers == null) DataRetriever.getPlayersInMatch(this);
        return participatingPlayers;
    }
}
