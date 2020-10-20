package io.github.tastac.bfj.components;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * <p>A player in a single match that has been queried from the Battlefields API.</p>
 *
 * @author Ocelot
 */
public class BFMatchParticipant
{
    @SerializedName("match_id")
    private final int matchId;
    @SerializedName("player_id")
    private final int playerId;

    public BFMatchParticipant(int matchId, int playerId)
    {
        this.matchId = matchId;
        this.playerId = playerId;
    }

    /**
     * @return The id of the match the player participated in
     */
    public int getMatchId()
    {
        return matchId;
    }

    /**
     * @return The id of the player in the match
     */
    public int getPlayerId()
    {
        return playerId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BFMatchParticipant that = (BFMatchParticipant) o;
        return this.matchId == that.matchId &&
                this.playerId == that.playerId;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.matchId, this.playerId);
    }

    @Override
    public String toString()
    {
        return "BFMatchParticipant{" +
                "matchId=" + this.matchId +
                ", playerId=" + this.playerId +
                '}';
    }
}
