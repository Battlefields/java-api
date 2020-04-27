package io.github.tastac.bfj.components;

import com.google.gson.annotations.SerializedName;
import io.github.tastac.bfj.DataRetriever;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>Information about a player in-game that has been queried from the Battlefields API.</p>
 *
 * @author Tastac
 */
public class BFPlayer
{
    private final int id;
    private final String uuid;
    private final String username;
    @SerializedName("last_seen")
    private final String lastSeen;

    public BFPlayer(int id, String uuid, String username, String lastSeen)
    {
        this.id = id;
        this.uuid = uuid;
        this.username = username;
        this.lastSeen = lastSeen;
    }

    /**
     * @return The id of this specific player. Separate from {@link #getUUID()}
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return The universally unique identifier of this player
     */
    public String getUUID()
    {
        return uuid;
    }

    /**
     * @return The username of this player
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @return The last time this player was seen on the server
     */
    public String getLastSeen()
    {
        return lastSeen;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BFPlayer)) return false;
        BFPlayer bfPlayer = (BFPlayer) o;
        return this.id == bfPlayer.id && this.uuid.equals(bfPlayer.uuid);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id, this.uuid);
    }

    @Override
    public String toString()
    {
        return "BFPlayer{" +
                "id=" + this.id +
                ", uuid='" + this.uuid + '\'' +
                ", username='" + this.username + '\'' +
                ", lastSeen='" + this.lastSeen + '\'' +
                '}';
    }
}
