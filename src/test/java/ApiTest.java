import io.github.tastac.bfj.BattlefieldsAPI;
import io.github.tastac.bfj.BattlefieldsApiBuilder;
import io.github.tastac.bfj.components.BFServerInfo;
import io.github.tastac.bfj.components.BFWeapon;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ApiTest
{
    private static void makeRequests(BattlefieldsAPI api) throws ExecutionException, InterruptedException
    {
        //        System.out.println(api.getServerInfo());
        //        System.out.println(api.getWeaponById(7));

        Future<String> serverStatusFuture = api.requestServerStatus();
        Future<BFServerInfo> serverInfoFuture = api.requestServerInfo();
        Future<BFWeapon> weaponFuture = api.requestWeaponByItemName("minecraft:bow");

        System.out.println(serverStatusFuture.get());
        System.out.println(serverInfoFuture.get());
        System.out.println(weaponFuture.get());
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
