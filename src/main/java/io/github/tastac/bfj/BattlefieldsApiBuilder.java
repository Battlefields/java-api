package io.github.tastac.bfj;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * <p>Creates new instances of {@link BattlefieldsAPI} through a builder format.</p>
 *
 * @author Ocelot
 */
public class BattlefieldsAPIBuilder
{
    private ExecutorService executor;
    private Consumer<Exception> exceptionConsumer;
    private long shutdownTimeout;
    private TimeUnit shutdownTimeoutUnit;
    private long cacheTime;
    private TimeUnit cacheTimeUnit;

    public BattlefieldsAPIBuilder()
    {
        this.executor = null;
        this.exceptionConsumer = Exception::printStackTrace;
        this.shutdownTimeout = 30;
        this.shutdownTimeoutUnit = TimeUnit.SECONDS;
        this.cacheTime = 5;
        this.cacheTimeUnit = TimeUnit.MINUTES;
    }

    /**
     * Sets the executor that will be used to make requests.
     *
     * @param executor The new executor to use
     */
    public BattlefieldsAPIBuilder setExecutor(ExecutorService executor)
    {
        this.executor = executor;
        return this;
    }

    /**
     * Sets the handler to be used when an exception is thrown by the {@link BattlefieldsAPI}.
     *
     * @param exceptionConsumer The consumer that handles the exception
     */
    public BattlefieldsAPIBuilder setExceptionConsumer(Consumer<Exception> exceptionConsumer)
    {
        this.exceptionConsumer = exceptionConsumer;
        return this;
    }

    /**
     * Sets the amount of time to wait for a shutdown. Used in {@link BattlefieldsAPI#close()}.
     *
     * @param timeout  The time to wait
     * @param timeUnit The unit timeout is provided in
     */
    public BattlefieldsAPIBuilder setShutdownTimeout(long timeout, TimeUnit timeUnit)
    {
        if (timeout < 0)
            throw new IllegalArgumentException("Timeout must be at least 0");
        this.shutdownTimeout = timeout;
        this.shutdownTimeoutUnit = timeUnit;
        return this;
    }

    /**
     * Sets the amount of time data is cached for.
     *
     * @param cacheTime The time to cache data
     * @param timeUnit  The unit cacheTime is provided in
     */
    public BattlefieldsAPIBuilder setCacheTime(long cacheTime, TimeUnit timeUnit)
    {
        if (cacheTime < 0)
            throw new IllegalArgumentException("Cache Time must be at least 0");
        this.cacheTime = cacheTime;
        this.cacheTimeUnit = timeUnit;
        return this;
    }

    /**
     * @return Builds a new standard {@link BattlefieldsAPI} with the provided parameters
     */
    public BattlefieldsAPI create()
    {
        return new BattlefieldsAPIImpl(this.executor != null ? this.executor : Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), task -> new Thread(task, "Battlefields API Worker")), this.exceptionConsumer, this.shutdownTimeout, this.shutdownTimeoutUnit, this.cacheTime, this.cacheTimeUnit);
    }
}
