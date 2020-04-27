package io.github.tastac.bfj.components;

import java.util.Objects;

/**
 * <p>Information about an emote in-game that has been queried from the Battlefields API.</p>
 *
 * @author Ocelot
 */
public class BFEmote
{
    private final int id;
    private final String name;
    private final boolean enabled;

    public BFEmote(int id, String name, boolean enabled)
    {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
    }

    /**
     * @return The id id this specific emote
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return The display name of this emote
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return Whether or not this emote is registered into the game
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BFEmote)) return false;
        BFEmote that = (BFEmote) o;
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
        return "BFEmote{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                ", enabled=" + this.enabled +
                '}';
    }
}
