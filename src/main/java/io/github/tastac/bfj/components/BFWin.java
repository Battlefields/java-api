package io.github.tastac.bfj.components;

import java.util.Objects;

/**
 * <p>A count of wins for a player that has been queried from the Battlefields API.</p>
 *
 * @author Ocelot
 */
public class BFWin
{
    private final String uuid;
    private final int wins;

    public BFWin(String uuid, int wins)
    {
        this.uuid = uuid;
        this.wins = wins;
    }

    /**
     * @return The id of the player with the wins
     */
    public String getUuid()
    {
        return uuid;
    }

    /**
     * @return The count of wins the player has
     */
    public int getWins()
    {
        return wins;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BFWin bfKill = (BFWin) o;
        return this.wins == bfKill.wins &&
                this.uuid.equals(bfKill.uuid);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.uuid, this.wins);
    }

    @Override
    public String toString()
    {
        return "BFWin{" +
                "uuid='" + this.uuid + '\'' +
                ", wins=" + this.wins +
                '}';
    }
}
