import io.github.tastac.bfj.BattlefieldsApi;
import io.github.tastac.bfj.BattlefieldsApiBuilder;
import io.github.tastac.bfj.components.BFServer;
import io.github.tastac.bfj.components.BFServerInfo;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ApiTest
{
    private static void makeNewRequests(BattlefieldsApi api) throws InterruptedException, ExecutionException
    {
        CompletableFuture<String[]> serverList = api.requestServerList();
        CompletableFuture<BFServer[]> serverStatusFuture = api.requestServerStatus();
        CompletableFuture<BFServerInfo> serverInfoFuture = api.requestServerInfo("us-west.battlefieldsmc.net");

        System.out.println("Server List: " + Arrays.toString(serverList.get()));
        System.out.println("Server Status: " + Arrays.toString(serverStatusFuture.get()));
        System.out.println("Server Info: " + serverInfoFuture.get());
    }

    public static void main(String[] args) throws Exception
    {
        BattlefieldsApi api = new BattlefieldsApiBuilder().create();

        long startTime = System.currentTimeMillis();
        makeNewRequests(api);

        System.out.println("Finished requests in " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println("Shutdown successfully? " + api.shutdown());
        System.out.println("Stopping!");
    }
}
