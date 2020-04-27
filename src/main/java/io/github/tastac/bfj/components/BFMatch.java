package io.github.tastac.bfj.components;

import io.github.tastac.bfj.DataRetriever;

import java.util.Objects;

/**
 * <p>Information about a match in-game that has been queried from the Battlefields API.</p>
 *
 * @author Tastac
 */
public class BFMatch
{
    private final int id;
    private final int number;
    private final String startDate;
    private final String endDate;
    private final int winningPlayerId;

    @Deprecated
    private int[] participatingPlayerIDs;
    @Deprecated
    private BFPlayer[] participatingPlayers;

    public BFMatch(int id, int number, String startDate, String endDate, int winningPlayerId)
    {
        this.id = id;
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
        this.winningPlayerId = winningPlayerId;
    }

    //TODO add documentation to all these methods

    public int getId() { return id; }

    public int getNumber() { return number; }

    public String getStartDate() { return startDate; }

    public String getEndDate() { return endDate; }

    public int getWinningPlayerId()
    {
        return winningPlayerId;
    }

    public BFPlayer getWinningPlayer() { return DataRetriever.getPlayerByID(this.winningPlayerId); }

    public int[] getParticipatingPlayerIDs()
    {
        if (participatingPlayerIDs == null) DataRetriever.getPlayersInMatch(this);
        return participatingPlayerIDs;
    }

    public BFPlayer[] getParticipatingPlayers()
    {
        if (participatingPlayerIDs == null) DataRetriever.getPlayersInMatch(this);
        if (participatingPlayers == null)
        {
            participatingPlayers = new BFPlayer[participatingPlayerIDs.length];
            for (int i : participatingPlayerIDs)
            {
                participatingPlayers[i] = DataRetriever.getPlayerByID(i);
            }
        }
        return participatingPlayers;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BFMatch)) return false;
        BFMatch bfMatch = (BFMatch) o;
        return this.id == bfMatch.id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
    }

    @Override
    public String toString()
    {
        return "BFMatch{" +
                "id=" + this.id +
                ", number=" + this.number +
                ", startDate='" + this.startDate + '\'' +
                ", endDate='" + this.endDate + '\'' +
                ", winningPlayer=" + this.getWinningPlayer() +
                '}';
    }
}
