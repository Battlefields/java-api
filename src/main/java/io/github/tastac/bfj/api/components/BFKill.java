package io.github.tastac.bfj.api.components;

import com.sun.javafx.geom.Vec3d;
import io.github.tastac.bfj.api.DataRetriever;

public class BFKill {

    private int ID;
    private int matchID;
    private int sourcePlayer;
    private int targetPlayer;
    private Vec3d sourcePos;
    private Vec3d targetPos;
    private int weaponID;

    public BFKill(int ID, int matchID, int sourceID, int targetID, Vec3d sourcePos, Vec3d targetPos, int weaponID){
        this.ID = ID;
        this.matchID = matchID;
        this.sourcePlayer = sourceID;
        this.targetPlayer = targetID;
        this.sourcePos = sourcePos;
        this.targetPos = targetPos;
        this.weaponID = weaponID;
    }

    public int getID() {
        return ID;
    }

    public BFMatch getMatch() {
        return DataRetriever.getMatchFromID(matchID);
    }

    public BFPlayer getSourcePlayer() {
        return DataRetriever.getPlayerByID(sourcePlayer);
    }

    public BFPlayer getTargetPlayer() {
        return DataRetriever.getPlayerByID(targetPlayer);
    }

    public Vec3d getSourcePos() {
        return sourcePos;
    }

    public Vec3d getTargetPos() {
        return targetPos;
    }

    public BFWeapon getWeapon() {
        return DataRetriever.getWeaponByID(weaponID);
    }
}
