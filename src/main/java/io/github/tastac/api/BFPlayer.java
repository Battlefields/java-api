package io.github.tastac.api;

import java.util.Date;

public class BFPlayer {

    private int ID;
    private String uuid;
    private String username;
    private Date lastSeen;

    public BFPlayer(int ID, String uuid, String username, Date lastSeen){
        this.ID = ID;
        this.uuid = uuid;
        this.username = username;
        this.lastSeen = lastSeen;
    }

    public int getID() {
        return ID;
    }

    public String getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public Date getLastSeen() {
        return lastSeen;
    }
}
