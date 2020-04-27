package io.github.tastac.bfj.components;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * <p>Information about an kill in-game that has been queried from the Battlefields API.</p>
 *
 * @author Tastac
 */
public class BFKill
{
    private final int id;
    @SerializedName("match_id")
    private final int matchId;
    @SerializedName("source_player")
    private final int sourcePlayerId;
    @SerializedName("target_player")
    private final int targetPlayerId;
    @SerializedName("weapon")
    private final int weaponId;
    @SerializedName("source_x")
    private final double sourcePosX;
    @SerializedName("source_y")
    private final double sourcePosY;
    @SerializedName("source_z")
    private final double sourcePosZ;
    @SerializedName("target_x")
    private final double targetPosX;
    @SerializedName("target_y")
    private final double targetPosY;
    @SerializedName("target_z")
    private final double targetPosZ;

    public BFKill(int id, int matchId, int sourcePlayerId, int targetPlayerId, int weaponId, double sourcePosX, double sourcePosY, double sourcePosZ, double targetPosX, double targetPosY, double targetPosZ)
    {
        this.id = id;
        this.matchId = matchId;
        this.sourcePlayerId = sourcePlayerId;
        this.targetPlayerId = targetPlayerId;
        this.weaponId = weaponId;
        this.sourcePosX = sourcePosX;
        this.sourcePosY = sourcePosY;
        this.sourcePosZ = sourcePosZ;
        this.targetPosX = targetPosX;
        this.targetPosY = targetPosY;
        this.targetPosZ = targetPosZ;
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
     * @return The id of the player that killed the target player
     */
    public int getSourcePlayerId()
    {
        return sourcePlayerId;
    }

    /**
     * @return The id of the player that was killed by the source player
     */
    public int getTargetPlayerId()
    {
        return targetPlayerId;
    }

    /**
     * @return The id of the weapon used to kill the target player
     */
    public int getWeaponId()
    {
        return weaponId;
    }

    /**
     * @return The exact x position where the source player was when the kill took place
     */
    public double getSourcePosX()
    {
        return sourcePosX;
    }

    /**
     * @return The exact y position where the source player was when the kill took place
     */
    public double getSourcePosY()
    {
        return sourcePosY;
    }

    /**
     * @return The exact z position where the source player was when the kill took place
     */
    public double getSourcePosZ()
    {
        return sourcePosZ;
    }

    /**
     * @return The exact x position where the target player was when the kill took place
     */
    public double getTargetPosX()
    {
        return targetPosX;
    }

    /**
     * @return The exact y position where the target player was when the kill took place
     */
    public double getTargetPosY()
    {
        return targetPosY;
    }

    /**
     * @return The exact z position where the target player was when the kill took place
     */
    public double getTargetPosZ()
    {
        return targetPosZ;
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
                ", match_id=" + this.matchId +
                ", source_player=" + this.sourcePlayerId +
                ", target_player=" + targetPlayerId +
                ", weapon=" + this.weaponId +
                ", sourcePos=(" + this.sourcePosX + ", " + this.sourcePosY + ", " + this.sourcePosZ + ")" +
                ", targetPos=(" + this.targetPosX + ", " + this.targetPosY + ", " + this.targetPosZ + ")" +
                '}';
    }
}
