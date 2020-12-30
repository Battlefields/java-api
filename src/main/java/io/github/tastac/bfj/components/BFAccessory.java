package io.github.tastac.bfj.components;

import com.google.gson.annotations.SerializedName;
import io.github.tastac.bfj.BFJ;

import java.util.Objects;

/**
 * <p>Information about an accessory in-game that has been queried from the Battlefields API.</p>
 *
 * @author Tastac
 */
public class BFAccessory
{
    private final int id;
    @SerializedName("accessory_type")
    private final int typeId;
    private final String name;
    private final String data;
    private final String enabled;
    private final String hidden;

    public BFAccessory(int id, int typeId, String name, String data, boolean enabled, boolean hidden)
    {
        this.id = id;
        this.typeId = typeId;
        this.name = name;
        this.data = data;
        this.enabled = enabled ? "1" : "0";
        this.hidden = hidden ? "1" : "0";
    }

    /**
     * @return The id id this specific accessory
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return id of the type of this accessory
     */
    public int getTypeId()
    {
        return typeId;
    }

    /**
     * @return The display name of this accessory
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return The data associated with this accessory
     */
    public String getData()
    {
        return data;
    }

    /**
     * @return Whether or not this accessory is registered into the game
     */
    public boolean isEnabled()
    {
        return "1".equals(this.enabled);
    }

    /**
     * @return Whether or not this accessory is hidden from normal users in-game
     */
    public boolean isHidden()
    {
        return "1".equals(this.hidden);
    }

    /**
     * @return A link to an online icon image
     */
    public String getImageURL()
    {
        return BFJ.BF_ICONS_URL + "/accessories/" + this.id + ".png";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BFAccessory)) return false;
        BFAccessory that = (BFAccessory) o;
        return this.id == that.id && this.typeId == that.typeId;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id, this.typeId);
    }

    @Override
    public String toString()
    {
        return "BFAccessory{" +
                "id=" + this.id +
                ", accessory_type=" + this.typeId +
                ", name='" + this.name + '\'' +
                ", data='" + this.data + '\'' +
                ", enabled=" + this.isEnabled() +
                ", hidden=" + this.isHidden() +
                '}';
    }
}
