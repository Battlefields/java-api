package io.github.tastac.bfj;

import io.github.tastac.bfj.components.BFServerInfo;
import io.github.tastac.bfj.components.BFWeapon;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * <p>A wrapper of the online Battlefields API. Requests for specific data can be made through this.</p>
 *
 * @author Ocelot
 */
@SuppressWarnings("unused")
public interface BattlefieldsAPI extends AutoCloseable
{
    /**
     * Clears the entire request cache and forces all new requests to access the API.
     */
    void clearCache();

    /**
     * <p>Fetches the server status from the API.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param handler The handler that will receive the result
     */
    default void requestServerStatus(Consumer<String> handler)
    {
        this.getExecutor().execute(() -> handler.accept(this.getServerStatus()));
    }

    /**
     * <p>Fetches the server status from the API.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @return The value that will exist at some point in the future
     */
    default Future<String> requestServerStatus()
    {
        return this.getExecutor().submit(this::getServerStatus);
    }

    /**
     * <p>Fetches the server status from the API.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @return The status of the battlefields east server
     */
    String getServerStatus();

    /**
     * <p>Fetches the server information from the API.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param handler The handler that will receive the result
     */
    default void requestServerInfo(Consumer<BFServerInfo> handler)
    {
        this.getExecutor().execute(() -> handler.accept(this.getServerInfo()));
    }

    /**
     * <p>Fetches the server information from the API.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @return The value that will exist at some point in the future
     */
    default Future<BFServerInfo> requestServerInfo()
    {
        return this.getExecutor().submit(this::getServerInfo);
    }

    /**
     * <p>Fetches the server information from the API.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @return The information about the server or null if the server did not respond. Use {@link #getServerStatus()} to determine if the server should be able to send data
     */
    @Nullable
    BFServerInfo getServerInfo();

    /**
     * <p>Fetches information about the weapon with the specified id from the API.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestWeapons(Consumer<BFWeapon[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getWeapons(queries)));
    }

    /**
     * <p>Fetches information about the weapon with the specified id from the API.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default Future<BFWeapon[]> requestWeapons(String... queries)
    {
        return this.getExecutor().submit(() -> this.getWeapons(queries));
    }

    /**
     * <p>Fetches information about the weapon with the specified id from the API.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The weapon information or null if the API request failed
     */
    BFWeapon[] getWeapons(String... queries);

    /**
     * @return The executor used to make API requests asynchronously
     */
    ExecutorService getExecutor();

    /**
     * Terminates the executor used to make API requests asynchronously.
     *
     * @return {@code true} if this executor terminated and {@code false} if the timeout elapsed before termination
     * @throws InterruptedException if interrupted while waiting
     */
    boolean shutdown() throws InterruptedException;

    @Override
    default void close() throws InterruptedException
    {
        this.shutdown();
    }
}
