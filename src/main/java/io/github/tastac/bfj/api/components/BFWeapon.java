package io.github.tastac.bfj.api.components;

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
}
