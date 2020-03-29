package io.github.tastac.bfj.components;

import java.util.Objects;

public class BFWeapon {

    private String itemName;
    private int ID;
    private int itemID;

    public BFWeapon(int ID, int itemID, String itemName){
        this.itemName = itemName;
        this.itemID = itemID;
        this.ID = ID;
    }

    //TODO add documentation to all these methods

    public String getItemName() {
        return itemName;
    }

    public int getID() {
        return ID;
    }

    public int getItemID() {
        return itemID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BFWeapon)) return false;
        BFWeapon bfWeapon = (BFWeapon) o;
        return ID == bfWeapon.ID &&
                itemID == bfWeapon.itemID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, itemID);
    }
}
