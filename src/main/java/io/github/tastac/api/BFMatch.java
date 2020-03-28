package io.github.tastac.api;

import java.time.LocalDateTime;

public class BFMatch {

    private int ID;
    private int number;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int winningPlayerID;

    public BFMatch(int ID, int number, LocalDateTime startDate, LocalDateTime endDate, int winningPlayerID){
        this.ID = ID;
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
        this.winningPlayerID = winningPlayerID;
    }

    public int getID() {
        return ID;
    }

    public int getNumber() {
        return number;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public BFPlayer getWinningPlayer() {
        return BFDataRetriever.getPlayerByID(winningPlayerID);
    }
}
