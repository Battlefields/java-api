package io.github.tastac.bfj.components;

import io.github.tastac.bfj.BFJ;

public class BFAccessory {

    private int ID;
    private int typeID;
    private String name;
    private String data;
    private boolean enabled;
    private boolean hidden;

    public BFAccessory(int ID, int typeID, String name, String data, boolean enabled, boolean hidden){
        this.ID = ID;
        this.typeID = typeID;
        this.name = name;
        this.data = data;
        this.enabled = enabled;
        this.hidden = hidden;
    }

    public int getID() {
        return ID;
    }

    public int getTypeID() {
        return typeID;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getImageURL(){
        return BFJ.URLPrefix +  "/accessories/" + ID + ".png";
    }
}
