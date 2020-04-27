import io.github.tastac.bfj.BattlefieldsAPI;
import io.github.tastac.bfj.BattlefieldsAPIBuilder;
import io.github.tastac.bfj.components.BFKill;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ApiTest
{
    private static void makeRequests(BattlefieldsAPI api) throws ExecutionException, InterruptedException
    {
        //        System.out.println(api.getServerInfo());
        //        System.out.println(api.getWeaponById(7));

        Future<Pair<String, Integer>[]> killsFuture = api.requestKills();
        Future<Pair<String, Integer>[]> winsFuture = api.requestWins();

        System.out.println("Most Kills: " + Arrays.stream(killsFuture.get()).max(Comparator.comparingInt(Pair::getRight)).orElse(null));
        System.out.println("Most Wins: " + Arrays.stream(winsFuture.get()).max(Comparator.comparingInt(Pair::getRight)).orElse(null));
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
