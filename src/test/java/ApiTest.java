import io.github.tastac.bfj.BattlefieldsAPI;
import io.github.tastac.bfj.BattlefieldsApiBuilder;
import io.github.tastac.bfj.components.BFWeapon;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ApiTest
{
    private static void makeRequests(BattlefieldsAPI api) throws ExecutionException, InterruptedException
    {
        //        System.out.println(api.getServerInfo());
        //        System.out.println(api.getWeaponById(7));

        Future<BFWeapon[]> serverStatusFuture = api.requestWeapons("item_name=minecraft:stick");

        System.out.println("Weapons: " + Arrays.toString(serverStatusFuture.get()));
    }

    public static void main(String[] args) throws Exception
    {
        BattlefieldsAPI api = new BattlefieldsApiBuilder().create();

        long startTime = System.currentTimeMillis();
        makeRequests(api);
        System.out.println("Finished requests in " + (System.currentTimeMillis() - startTime) + "ms");

        System.out.println("Shutdown successfully? " + api.shutdown());
        System.out.println("Stopping!");
    }
}
