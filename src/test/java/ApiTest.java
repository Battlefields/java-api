import io.github.tastac.bfj.BattlefieldsAPI;
import io.github.tastac.bfj.BattlefieldsAPIBuilder;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class ApiTest
{
    private static void makeRequests(BattlefieldsAPI api) throws InterruptedException, ExecutionException
    {
        System.out.println("Weapons: " + Arrays.toString(api.requestWeapons().get()));
        System.out.println("Accessories: " + Arrays.toString(api.requestAccessories().get()));
        System.out.println("Accessory Types: " + Arrays.toString(api.requestAccessoryTypes().get()));
        System.out.println("Linked Discords: " + Arrays.toString(api.requestLinkedDiscord().get()));
        System.out.println("Owned Emotes: " + Arrays.toString(api.requestOwnedEmotes().get()));
        System.out.println("Emotes: " + Arrays.toString(api.requestEmotes().get()));
        System.out.println("Weapon Stats: " + Arrays.toString(api.requestWeaponStats("match_id=19").get()));
        System.out.println("Server Status: " + api.requestServerInfo().get());
    }

    public static void main(String[] args) throws Exception
    {
        BattlefieldsAPI api = new BattlefieldsAPIBuilder().create();

        long startTime = System.currentTimeMillis();
        makeRequests(api);
        System.out.println("Finished requests in " + (System.currentTimeMillis() - startTime) + "ms");

        System.out.println("Shutdown successfully? " + api.shutdown());
        System.out.println("Stopping!");
    }
}
