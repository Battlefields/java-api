package io.github.tastac.bfj.components;

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

    public BFMatch(int id, int number, String startDate, String endDate, int winningPlayerId)
    {
        this.id = id;
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
        this.winningPlayerId = winningPlayerId;
    }

    /**
     * @return The id of this specific match
     */
    public int getId() { return id; }

    /**
     * @return The number of this match
     */
    public int getNumber() { return number; }

    /**
     * @return The starting date of this match
     */
    public String getStartDate() { return startDate; }

    /**
     * @return The ending date of this match
     */
    public String getEndDate() { return endDate; }

    /**
     * @return The id of the player that won
     */
    public int getWinningPlayerId()
    {
        return winningPlayerId;
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
                ", winningPlayer=" + this.winningPlayerId +
                '}';
    }
}
