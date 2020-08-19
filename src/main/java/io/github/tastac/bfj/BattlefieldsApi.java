package io.github.tastac.bfj;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.tastac.bfj.components.*;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * <p>A wrapper of the online Battlefields API. Requests for specific data can be made through this.</p>
 *
 * @author Ocelot
 */
public interface BattlefieldsApi extends AutoCloseable
{
    /**
     * Clears the entire request cache and forces all new requests to access the API.
     */
    void clearCache();

    /**
     * <p>Fetches the specified information from the API.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param handler The handler that will receive the result
     */
    default void request(Consumer<JsonArray> handler, BattlefieldsApiTable table, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.get(table, queries)));
    }

    /**
     * <p>Fetches the specified information from the API.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<JsonArray> request(BattlefieldsApiTable table, String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.get(table, queries), this.getExecutor());
    }

    /**
     * <p>Fetches the specified information from the API.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param table   The table to query
     * @param queries The filter to use when searching for data
     * @return The requested information or null if the API request failed
     */
    @Nullable
    JsonArray get(BattlefieldsApiTable table, String... queries);

    /**
     * <p>Fetches the specified cosmetic model from the API.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param handler The handler that will receive the result
     */
    default void requestCosmeticModel(Consumer<JsonObject> handler, String modelName)
    {
        this.getExecutor().execute(() -> handler.accept(this.getCosmeticModel(modelName)));
    }

    /**
     * <p>Fetches the specified cosmetic model from the API.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<JsonObject> requestCosmeticModel(String modelName)
    {
        return CompletableFuture.supplyAsync(() -> this.getCosmeticModel(modelName), this.getExecutor());
    }

    /**
     * <p>Fetches the specified cosmetic model from the API.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param modelName The name of the model to get
     * @return The cosmetic model data read from online
     */
    JsonObject getCosmeticModel(String modelName);

    /**
     * <p>Fetches the specified cosmetic model md5 hash from the API.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param handler The handler that will receive the result
     */
    default void requestCosmeticModelHash(Consumer<String> handler, String modelName)
    {
        this.getExecutor().execute(() -> handler.accept(this.getCosmeticModelHash(modelName)));
    }

    /**
     * <p>Fetches the specified cosmetic model md5 hash from the API.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<String> requestCosmeticModelHash(String modelName)
    {
        return CompletableFuture.supplyAsync(() -> this.getCosmeticModelHash(modelName), this.getExecutor());
    }

    /**
     * <p>Fetches the specified cosmetic model md5 hash from the API.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param modelName The name of the model to get the hash for
     * @return The cosmetic model hash read from online
     */
    String getCosmeticModelHash(String modelName);

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
    default CompletableFuture<String> requestServerStatus()
    {
        return CompletableFuture.supplyAsync(this::getServerStatus, this.getExecutor());
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
    default CompletableFuture<BFServerInfo> requestServerInfo()
    {
        return CompletableFuture.supplyAsync(this::getServerInfo, this.getExecutor());
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
     * <p>Fetches information about kills with the specified queries.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestKills(Consumer<Pair<String, Integer>[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getKills(queries)));
    }

    /**
     * <p>Fetches information about kills with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<Pair<String, Integer>[]> requestKills(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getKills(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information about kills with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The weapon information or an empty array if the API request failed
     */
    Pair<String, Integer>[] getKills(String... queries);

    /**
     * <p>Fetches information about wins with the specified queries.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestWins(Consumer<Pair<String, Integer>[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getWins(queries)));
    }

    /**
     * <p>Fetches information about wins with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<Pair<String, Integer>[]> requestWins(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getWins(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information about wins with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The win information or an empty array  if the API request failed
     */
    Pair<String, Integer>[] getWins(String... queries);

    /**
     * <p>Fetches information about players with the specified queries.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestPlayers(Consumer<BFPlayer[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getPlayers(queries)));
    }

    /**
     * <p>Fetches information about players with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<BFPlayer[]> requestPlayers(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getPlayers(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information about players with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The player information or an empty array if the API request failed
     */
    BFPlayer[] getPlayers(String... queries);

    /**
     * <p>Fetches information about matches with the specified queries.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestMatches(Consumer<BFMatch[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getMatches(queries)));
    }

    /**
     * <p>Fetches information about matches with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<BFMatch[]> requestMatches(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getMatches(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information about matches with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The match information or an empty array if the API request failed
     */
    BFMatch[] getMatches(String... queries);

    /**
     * <p>Fetches information about accessories players own with the specified queries.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestOwnedAccessories(Consumer<Pair<String, Integer>[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getOwnedAccessories(queries)));
    }

    /**
     * <p>Fetches information about accessories players own with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<Pair<String, Integer>[]> requestOwnedAccessories(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getOwnedAccessories(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information about accessories players own with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The accessory information or an empty array if the API request failed
     */
    Pair<String, Integer>[] getOwnedAccessories(String... queries);

    /**
     * <p>Fetches information all accessories that exist with the specified queries.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestAccessories(Consumer<BFAccessory[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getAccessories(queries)));
    }

    /**
     * <p>Fetches information all accessories that exist with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<BFAccessory[]> requestAccessories(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getAccessories(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information all accessories that exist with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The accessory information or an empty array if the API request failed
     */
    BFAccessory[] getAccessories(String... queries);

    /**
     * <p>Fetches information all accessory types that exist with the specified queries.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestAccessoryTypes(Consumer<Pair<Integer, String>[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getAccessoryTypes(queries)));
    }

    /**
     * <p>Fetches information all accessory types that exist with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<Pair<Integer, String>[]> requestAccessoryTypes(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getAccessoryTypes(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information all accessory types that exist with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The accessory type information or an empty array if the API request failed
     */
    Pair<Integer, String>[] getAccessoryTypes(String... queries);

    /**
     * <p>Fetches information about weapons with the specified queries.</p>
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
     * <p>Fetches information about weapons with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<BFWeapon[]> requestWeapons(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getWeapons(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information about weapons with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The weapon information or an empty array if the API request failed
     */
    BFWeapon[] getWeapons(String... queries);

    /**
     * <p>Fetches information all weapon stats that have been collected exist with the specified queries.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestWeaponStats(Consumer<BFWeaponStats[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getWeaponStats(queries)));
    }

    /**
     * <p>Fetches information all weapon stats that have been collected exist with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<BFWeaponStats[]> requestWeaponStats(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getWeaponStats(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information all weapon stats that have been collected exist with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The weapon information or an empty array if the API request failed
     */
    BFWeaponStats[] getWeaponStats(String... queries);

    /**
     * <p>Fetches information players that participated in each match with the specified queries.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestMatchParticipants(Consumer<Pair<Integer, Integer>[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getMatchParticipants(queries)));
    }

    /**
     * <p>Fetches information players that participated in each match with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<Pair<Integer, Integer>[]> requestMatchParticipants(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getMatchParticipants(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information players that participated in each match with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The weapon information or an empty array if the API request failed
     */
    Pair<Integer, Integer>[] getMatchParticipants(String... queries);

    /**
     * <p>Fetches information about each kill in each match with the specified queries.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestMatchKills(Consumer<BFKill[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getMatchKills(queries)));
    }

    /**
     * <p>Fetches information about each kill in each match with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<BFKill[]> requestMatchKills(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getMatchKills(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information about each kill in each match with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The weapon information or an empty array if the API request failed
     */
    BFKill[] getMatchKills(String... queries);

    /**
     * <p>Fetches information about emotes players own with the specified queries.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestOwnedEmotes(Consumer<Pair<String, Integer>[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getOwnedEmotes(queries)));
    }

    /**
     * <p>Fetches information about emotes players own with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<Pair<String, Integer>[]> requestOwnedEmotes(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getOwnedEmotes(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information about emotes players own with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The accessory information or an empty array if the API request failed
     */
    Pair<String, Integer>[] getOwnedEmotes(String... queries);

    /**
     * <p>Fetches information all emotes that exist with the specified queries.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestEmotes(Consumer<BFEmote[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getEmotes(queries)));
    }

    /**
     * <p>Fetches information all emotes that exist with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<BFEmote[]> requestEmotes(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getEmotes(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information all emotes that exist with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The accessory information or an empty array if the API request failed
     */
    BFEmote[] getEmotes(String... queries);

    /**
     * <p>Fetches information about each player and their linked discord id with the specified queries.</p>
     * <p>This method is asynchronous and will call the provided handler when the value is received.</p>
     *
     * @param queries The filter to use when searching for data
     * @param handler The handler that will receive the result
     */
    default void requestLinkedDiscord(Consumer<Pair<String, Long>[]> handler, String... queries)
    {
        this.getExecutor().execute(() -> handler.accept(this.getLinkedDiscord(queries)));
    }

    /**
     * <p>Fetches information about each player and their linked discord id with the specified queries.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The value that will exist at some point in the future
     */
    default CompletableFuture<Pair<String, Long>[]> requestLinkedDiscord(String... queries)
    {
        return CompletableFuture.supplyAsync(() -> this.getLinkedDiscord(queries), this.getExecutor());
    }

    /**
     * <p>Fetches information about each player and their linked discord id with the specified queries.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param queries The filter to use when searching for data
     * @return The weapon information or an empty array if the API request failed
     */
    Pair<String, Long>[] getLinkedDiscord(String... queries);

    /**
     * @return The executor used to make API requests asynchronously
     */
    Executor getExecutor();

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
