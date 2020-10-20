package io.github.tastac.bfj.components;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * <p>A linked minecraft and discord that has been queried from the Battlefields API.</p>
 *
 * @author Ocelot
 */
public class BFLinkedDiscord
{
    private final String uuid;
    @SerializedName("discord_id")
    private final long discordId;

    public BFLinkedDiscord(String uuid, long discordId)
    {
        this.uuid = uuid;
        this.discordId = discordId;
    }

    public String getUUID()
    {
        return uuid;
    }

    public long getDiscordId()
    {
        return discordId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BFLinkedDiscord that = (BFLinkedDiscord) o;
        return this.discordId == that.discordId &&
                this.uuid.equals(that.uuid);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.uuid, this.discordId);
    }

    @Override
    public String toString()
    {
        return "BFLinkedDiscord{" +
                "uuid='" + this.uuid + '\'' +
                ", discordId=" + this.discordId +
                '}';
    }
}
