package io.github.tastac.bfj.components;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * <p>Information about firing a gun in-game that has been queried from the Battlefields API.</p>
 *
 * @author Tastac
 */
public class BFWeaponStats
{
    private final int id;
    @SerializedName("match_id")
    private final int matchId;
    @SerializedName("player_id")
    private final int playerId;
    @SerializedName("weapon_id")
    private final int weaponId;
    @SerializedName("shots_fired")
    private final int shotsFired;
    @SerializedName("shots_hit")
    private final int shotsHit;

    public BFWeaponStats(int id, int matchId, int playerId, int weaponId, int shotsFired, int shotsHit)
    {
        this.id = id;
        this.matchId = matchId;
        this.playerId = playerId;
        this.weaponId = weaponId;
        this.shotsFired = shotsFired;
        this.shotsHit = shotsHit;
    }

    /**
     * @return The id id this specific weapon statistic
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return The id of the match this took place in
     */
    public int getMatchId()
    {
        return matchId;
    }

    /**
     * @return The id of the player that used the weapon
     */
    public int getPlayerId()
    {
        return playerId;
    }

    /**
     * @return The id of the weapon used
     */
    public int getWeaponId()
    {
        return weaponId;
    }

    /**
     * @return The amount of bullets fired by a gun if this weapon is a gun
     */
    public int getShotsFired()
    {
        return shotsFired;
    }

    /**
     * @return The amount of shots that hit a target
     */
    public int getShotsHit()
    {
        return shotsHit;
    }

    /**
     * @return The percentage accuracy of the player firing this weapon
     */
    public float getAccuracy()
    {
        return this.shotsFired != 0f ? (float) this.shotsHit / (float) this.shotsFired : 0f;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BFWeaponStats)) return false;
        BFWeaponStats that = (BFWeaponStats) o;
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
        return "BFWeaponStats{" +
                "id=" + this.id +
                ", match=" + this.matchId +
                ", player=" + this.playerId +
                ", weapon=" + this.weaponId +
                ", shotsFired=" + this.shotsFired +
                ", shotsHit=" + this.shotsHit +
                ", accuracy=" + this.getAccuracy() +
                '}';
    }
}
