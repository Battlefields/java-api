package io.github.tastac.bfj.components;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * <p>A single owned accessory for a player that has been queried from the Battlefields API.</p>
 *
 * @author Ocelot
 */
public class BFOwnedAccessory
{
    private final String uuid;
    @SerializedName("accessory_id")
    private final int accessoryId;

    public BFOwnedAccessory(String uuid, int accessoryId)
    {
        this.uuid = uuid;
        this.accessoryId = accessoryId;
    }

    /**
     * @return The id of the player with the accessory
     */
    public String getUuid()
    {
        return uuid;
    }

    /**
     * @return The id of the accessory the player has
     */
    public int getAccessoryId()
    {
        return accessoryId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BFOwnedAccessory bfKill = (BFOwnedAccessory) o;
        return this.accessoryId == bfKill.accessoryId &&
                this.uuid.equals(bfKill.uuid);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.uuid, this.accessoryId);
    }

    @Override
    public String toString()
    {
        return "BFOwnedAccessory{" +
                "uuid='" + this.uuid + '\'' +
                ", accessoryId=" + this.accessoryId +
                '}';
    }
}
