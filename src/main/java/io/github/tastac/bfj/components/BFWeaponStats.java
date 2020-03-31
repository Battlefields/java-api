package io.github.tastac.bfj.components;

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
}
