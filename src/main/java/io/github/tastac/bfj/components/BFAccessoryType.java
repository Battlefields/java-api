package io.github.tastac.bfj.components;

/**
 * <p>A single type of accessory that has been queried from the Battlefields API.</p>
 *
 * @author Ocelot
 */
public class BFAccessoryType
{
    private final int id;
    private final String name;

    public BFAccessoryType(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    /**
     * @return The id of this accessory type
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return The name id of this type
     */
    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BFAccessoryType that = (BFAccessoryType) o;
        return id == that.id;
    }

    @Override
    public int hashCode()
    {
        return this.id;
    }

    @Override
    public String toString()
    {
        return "BFAccessoryType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
