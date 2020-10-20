import io.github.tastac.bfj.BattlefieldsApi;
import io.github.tastac.bfj.BattlefieldsApiBuilder;
import io.github.tastac.bfj.components.*;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ApiTest
{
    private static void makeOldRequests(BattlefieldsApi api) throws InterruptedException, ExecutionException
    {
        System.out.println("Weapons: " + Arrays.toString(api.requestWeapons().get()));
        System.out.println("Accessories: " + Arrays.toString(api.requestAccessories().get()));
        System.out.println("Accessory Types: " + Arrays.toString(api.requestAccessoryTypes().get()));
        System.out.println("Linked Discords: " + Arrays.toString(api.requestLinkedDiscord().get()));
        System.out.println("Owned Emotes: " + Arrays.toString(api.requestOwnedEmotes().get()));
        System.out.println("Emotes: " + Arrays.toString(api.requestEmotes().get()));
        System.out.println("Weapon Stats: " + Arrays.toString(api.requestWeaponStats("match_id=19").get()));
        System.out.println("Server Status: " + api.requestServerStatus().get());
        System.out.println("Server Info: " + api.requestServerInfo().get());
    }

    private static void makeNewRequests(BattlefieldsApi api) throws InterruptedException, ExecutionException
    {
        CompletableFuture<BFWeapon[]> weaponsFuture = api.requestWeapons();
        CompletableFuture<BFAccessory[]> accessoriesFuture = api.requestAccessories();
        CompletableFuture<BFAccessoryType[]> accessoryTypesFuture = api.requestAccessoryTypes();
        CompletableFuture<BFLinkedDiscord[]> linkedDiscordFuture = api.requestLinkedDiscord();
        CompletableFuture<BFOwnedEmote[]> ownedEmotesFuture = api.requestOwnedEmotes();
        CompletableFuture<BFEmote[]> emotesFuture = api.requestEmotes();
        CompletableFuture<BFWeaponStats[]> weaponStatsFuture = api.requestWeaponStats("match_id=19");
        CompletableFuture<String> serverStatusFuture = api.requestServerStatus();
        CompletableFuture<BFServerInfo> serverInfoFuture = api.requestServerInfo();

        CompletableFuture.allOf(weaponsFuture, accessoriesFuture, accessoryTypesFuture, linkedDiscordFuture, ownedEmotesFuture, emotesFuture, weaponStatsFuture, serverStatusFuture, serverInfoFuture).join();

        System.out.println("Weapons: " + Arrays.toString(weaponsFuture.get()));
        System.out.println("Accessories: " + Arrays.toString(accessoriesFuture.get()));
        System.out.println("Accessory Types: " + Arrays.toString(accessoryTypesFuture.get()));
        System.out.println("Linked Discords: " + Arrays.toString(linkedDiscordFuture.get()));
        System.out.println("Owned Emotes: " + Arrays.toString(ownedEmotesFuture.get()));
        System.out.println("Emotes: " + Arrays.toString(emotesFuture.get()));
        System.out.println("Weapon Stats: " + Arrays.toString(weaponStatsFuture.get()));
        System.out.println("Server Status: " + serverStatusFuture.get());
        System.out.println("Server Info: " + serverInfoFuture.get());
    }

    public static void main(String[] args) throws Exception
    {
        BattlefieldsApi api = new BattlefieldsApiBuilder().create();

        long oldStartTime = System.currentTimeMillis();
        makeOldRequests(api);

        System.out.println("Clearing cache...");
        api.clearCache();

        long newStartTime = System.currentTimeMillis();
        makeNewRequests(api);

        System.out.println("Finished old requests in " + (System.currentTimeMillis() - oldStartTime) + "ms and new requests in " + (System.currentTimeMillis() - newStartTime) + "ms");
        System.out.println("Shutdown successfully? " + api.shutdown());
        System.out.println("Stopping!");
    }
}
