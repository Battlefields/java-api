package io.github.tastac.bfj.components;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * <p>Information about a weapon in-game that has been queried from the Battlefields API.</p>
 *
 * @author Tastac
 */
public class BFWeapon
{
    private final int id;
    @SerializedName("item_id")
    private final int itemId;
    @SerializedName("item_name")
    private final String itemName;

    public BFWeapon(int id, int itemId, String itemName)
    {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
    }

    //TODO add documentation to all these methods

    /**
     * @return The id of this specific weapon
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return The id of the specific item used as a weapon
     */
    public int getItemId()
    {
        return itemId;
    }

    /**
     * @return The name of the item used as a weapon
     */
    public String getItemName()
    {
        return itemName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BFWeapon)) return false;
        BFWeapon bfWeapon = (BFWeapon) o;
        return this.id == bfWeapon.id && this.itemId == bfWeapon.itemId;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id, this.itemId);
    }

    @Override
    public String toString()
    {
        return "BFWeapon{" +
                "id=" + this.id +
                ", itemId=" + this.itemId +
                ", itemName='" + this.itemName + '\'' +
                '}';
    }
}
