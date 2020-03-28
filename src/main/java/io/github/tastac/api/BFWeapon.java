package io.github.tastac.api;

public class BFWeapon {

    public String itemName;
    public int ID;
    public int itemID;

    public BFWeapon(int ID, int itemID, String itemName){
        this.itemName = itemName;
        this.itemID = itemID;
        this.ID = ID;
    }

}
