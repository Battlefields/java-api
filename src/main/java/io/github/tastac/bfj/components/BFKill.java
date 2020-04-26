package io.github.tastac.bfj.components;

import com.sun.javafx.geom.Vec3d;
import io.github.tastac.bfj.DataRetriever;

import java.util.Objects;

/**
 * <p>Information about an kill in-game that has been queried from the Battlefields API.</p>
 *
 * @author Tastac
 */
public class BFKill
{
    private final int id;
    private final int matchId;
    private final int sourcePlayerId;
    private final int targetPlayerId;
    private final int weaponId;
    private final Vec3d sourcePos;
    private final Vec3d targetPos;

    public BFKill(int id, int matchId, int sourcePlayerId, int targetPlayerId, int weaponId, Vec3d sourcePos, Vec3d targetPos)
    {
        this.id = id;
        this.matchId = matchId;
        this.sourcePlayerId = sourcePlayerId;
        this.targetPlayerId = targetPlayerId;
        this.weaponId = weaponId;
        this.sourcePos = sourcePos;
        this.targetPos = targetPos;
    }

    /**
     * @return The id id this specific kill
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return The id of the match this kill took place in
     */
    public int getMatchId()
    {
        return matchId;
    }

    /**
     * @return The match this kill took place in
     */
    public BFMatch getMatch()
    {
        return DataRetriever.getMatchFromID(this.matchId);
    }

    /**
     * @return The id of the player that killed the target player
     */
    public int getSourcePlayerId()
    {
        return sourcePlayerId;
    }

    /**
     * @return The player that killed the target player
     */
    public BFPlayer getSourcePlayer()
    {
        return DataRetriever.getPlayerByID(this.sourcePlayerId);
    }

    /**
     * @return The id of the player that was killed by the source player
     */
    public int getTargetPlayerId()
    {
        return targetPlayerId;
    }

    /**
     * @return The player that was killed by the source player
     */
    public BFPlayer getTargetPlayer()
    {
        return DataRetriever.getPlayerByID(targetPlayerId);
    }

    /**
     * @return The id of the weapon used to kill the target player
     */
    public int getWeaponId()
    {
        return weaponId;
    }

    /**
     * @return The weapon used to kill the target player
     */
    public BFWeapon getWeapon()
    {
        return DataRetriever.getWeaponByID(this.weaponId);
    }

    /**
     * @return Exactly where the source player was when the kill took place
     */
    public Vec3d getSourcePos()
    {
        return sourcePos;
    }

    /**
     * @return Exactly where the target player was when the kill took place
     */
    public Vec3d getTargetPos()
    {
        return targetPos;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BFKill)) return false;
        BFKill that = (BFKill) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
    }

    @Override
    public String toString()
    {
        return "BFKill{" +
                "id=" + this.id +
                ", matchId=" + this.matchId +
                ", sourcePlayerId=" + this.sourcePlayerId +
                ", targetPlayerId=" + targetPlayerId +
                ", weaponId=" + this.weaponId +
                ", sourcePos=" + this.sourcePos +
                ", targetPos=" + this.targetPos +
                '}';
    }
}
