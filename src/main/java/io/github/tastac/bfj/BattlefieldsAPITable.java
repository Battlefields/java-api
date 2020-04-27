package io.github.tastac.bfj;

/**
 * <p>All the tables in the Battlefields API that can be searched through.</p>
 *
 * @author Ocelot
 */
public enum BattlefieldsAPITable
{
    KILLS("kills"),
    WINS("wins"),
    PLAYERS("players"),
    MATCHES("matches"),
    OWNED_ACCESSORIES("owned_accessories"),
    ACCESSORIES("accessories"),
    ACCESSORY_TYPES("accessory_types"),
    WEAPONS("weapons"),
    WEAPON_STATS("weapon_stats"),
    MATCH_PARTICIPANTS("match_participants"),
    MATCH_KILLS("match_kills"),
    OWNED_EMOTES("owned_emotes"),
    EMOTES("emotes"),
    LINKED_DISCORD("linked_discord");

    private final String table;

    BattlefieldsAPITable(String table)
    {
        this.table = table;
    }

    /**
     * @return The name of this table when searching
     */
    public String getTable()
    {
        return table;
    }
}
