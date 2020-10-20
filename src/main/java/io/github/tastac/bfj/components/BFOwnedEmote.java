package io.github.tastac.bfj.components;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * <p>A single owned emote for a player that has been queried from the Battlefields API.</p>
 *
 * @author Ocelot
 */
public class BFOwnedEmote
{
    private final String uuid;
    @SerializedName("emote_id")
    private final int emoteId;

    public BFOwnedEmote(String uuid, int emoteId)
    {
        this.uuid = uuid;
        this.emoteId = emoteId;
    }

    /**
     * @return The id of the player with the emote
     */
    public String getUuid()
    {
        return uuid;
    }

    /**
     * @return The id of the emote the player has
     */
    public int getEmoteId()
    {
        return emoteId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BFOwnedEmote bfKill = (BFOwnedEmote) o;
        return this.emoteId == bfKill.emoteId &&
                this.uuid.equals(bfKill.uuid);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.uuid, this.emoteId);
    }

    @Override
    public String toString()
    {
        return "BFOwnedEmote{" +
                "uuid='" + this.uuid + '\'' +
                ", emoteId=" + this.emoteId +
                '}';
    }
}
