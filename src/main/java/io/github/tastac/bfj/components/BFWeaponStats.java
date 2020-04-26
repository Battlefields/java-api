package io.github.tastac.bfj.components;

import java.util.Objects;

public class BFWeaponStats {

    private int ID;
    private int matchID;
    private int playerID;
    private int weaponID;
    private int shotsFired;
    private int shotsHit;

    public BFWeaponStats(int ID, int matchID, int playerID, int weaponID, int shotsFired, int shotsHit){
        this.ID = ID;
        this.matchID = matchID;
        this.playerID = playerID;
        this.weaponID = weaponID;
        this.shotsFired = shotsFired;
        this.shotsHit = shotsHit;
    }

    public int getID() {
        return ID;
    }

    public int getMatchID() {
        return matchID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getWeaponID() {
        return weaponID;
    }

    public int getShotsFired() {
        return shotsFired;
    }

    public int getShotsHit() {
        return shotsHit;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BFWeaponStats)) return false;
        BFWeaponStats that = (BFWeaponStats) o;
        return this.ID == that.ID;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.ID);
    }

    @Override
    public String toString()
    {
        // TODO add objects instead of ids
        return "BFWeaponStats{" +
                "id=" + this.ID +
                ", matchID=" + this.matchID +
                ", playerID=" + this.playerID +
                ", weaponID=" + this.weaponID +
                ", shotsFired=" + this.shotsFired +
                ", shotsHit=" + this.shotsHit +
                '}';
    }
}
