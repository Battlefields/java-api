package io.github.tastac.bfj.components;

import java.util.Objects;

/**
 * <p>Information about firing a gun in-game that has been queried from the Battlefields API.</p>
 *
 * @author Tastac
 */
public class BFWeaponStats
{

    private final int id;
    private final int matchId;
    private final int playerId;
    private final int weaponId;
    private final int shotsFired;
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
     * @return The id id this specific statistic
     */
    public int getId()
    {
        return id;
    }

    public int getMatchId()
    {
        return matchId;
    }

    public int getPlayerId()
    {
        return playerId;
    }

    public int getWeaponId()
    {
        return weaponId;
    }

    public int getShotsFired()
    {
        return shotsFired;
    }

    public int getShotsHit()
    {
        return shotsHit;
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
        // TODO add objects instead of ids
        return "BFWeaponStats{" +
                "id=" + this.id +
                ", match=" + this.matchId +
                ", player=" + this.playerId +
                ", weapon=" + this.weaponId +
                ", shotsFired=" + this.shotsFired +
                ", shotsHit=" + this.shotsHit +
                '}';
    }
}
