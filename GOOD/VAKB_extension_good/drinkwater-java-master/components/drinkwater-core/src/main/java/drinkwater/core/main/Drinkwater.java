package drinkwater.core.main;

import drinkwater.ApplicationOptions;
import drinkwater.core.DrinkWaterApplication;
import org.apache.camel.support.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static drinkwater.ApplicationOptionsBuilder.options;

/**
 * Created by A406775 on 2/01/2017.
 */
public class Drinkwater extends ServiceSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(Drinkwater.class.getName());
    protected static final int UNINITIALIZED_EXIT_CODE = Integer.MIN_VALUE;
    protected static final int DEFAULT_EXIT_CODE = 0;
    protected final CountDownLatch latch = new CountDownLatch(1);
    protected final AtomicBoolean completed = new AtomicBoolean(false);
    protected final AtomicInteger exitCode = new AtomicInteger(UNINITIALIZED_EXIT_CODE);
    protected long duration = -1;
    protected TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    protected boolean trace;
    protected boolean hangupInterceptorEnabled = true;
    protected int durationHitExitCode = DEFAULT_EXIT_CODE;
    private DrinkWaterApplication dwApplication;

    private Drinkwater(ApplicationOptions options){
        dwApplication = DrinkWaterApplication.create(options);
    }

    private Drinkwater(String name, ApplicationOptions options){
        dwApplication = DrinkWaterApplication.create(name, options);
    }

    public static void run(String name, ApplicationOptions options) throws Exception {
        new Drinkwater(name, options).run();
    }

    public static void run(ApplicationOptions options) throws Exception {
        new Drinkwater(options).run();
    }

    public static void run(Class applicationBuilderClass) throws Exception {
        new Drinkwater(options().use(applicationBuilderClass)).run();
    }

    private void run() throws Exception {
        if (!completed.get()) {
            internalBeforeStart();
            // if we have an issue starting then propagate the exception to caller
            start();
            try {
                waitUntilCompleted();
                internalBeforeStop();
                beforeStop();
                stop();
                afterStop();
            } catch (Exception e) {
                // however while running then just log errors
                LOG.error("Failed: " + e.getMessage());
            }
        }
    }


    private void internalBeforeStart() {
        if (hangupInterceptorEnabled) {
            Runtime.getRuntime().addShutdownHook(new HangupInterceptor(this));
        }
    }

    /**
     * Callback to run custom logic before CamelContext route being stopped.
     * <p/>
     * It route recommended to use {@link org.apache.camel.main.MainListener} instead.
     */
    protected void beforeStop() throws Exception {
    }

    /**
     * Callback to run custom logic after CamelContext has been stopped.
     * <p/>
     * It route recommended to use {@link org.apache.camel.main.MainListener} instead.
     */
    protected void afterStop() throws Exception {
    }

    private void internalBeforeStop() {
    }

    /**
     * Marks this process as being completed.
     */
    public void completed() {
        completed.set(true);
        exitCode.compareAndSet(UNINITIALIZED_EXIT_CODE, DEFAULT_EXIT_CODE);
        latch.countDown();
    }


    /**
     * Sets the duration to run the application for in milliseconds until it
     * should be terminated. Defaults to -1. Any value <= 0 will run forever.
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }


    protected void doStop() throws Exception {

        this.dwApplication.stop();
        // call completed to properly stop as we count down the waiting latch
        completed();


    }

    protected void doStart() throws Exception {

        this.dwApplication.start();
    }

    protected void waitUntilCompleted() {
        while (!completed.get()) {
            try {
                if (duration > 0) {
                    TimeUnit unit = getTimeUnit();
                    LOG.info("Waiting for: " + duration + " " + unit);
                    latch.await(duration, unit);
                    exitCode.compareAndSet(UNINITIALIZED_EXIT_CODE, durationHitExitCode);
                    completed.set(true);
                } else {
                    latch.await();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * A class for intercepting the hang up signal and do a graceful shutdown of the Camel.
     */
    private static final class HangupInterceptor extends Thread {
        final Drinkwater mainInstance;
        Logger log = LoggerFactory.getLogger(this.getClass().getName());

        HangupInterceptor(Drinkwater main) {
            mainInstance = main;
        }

        @Override
        public void run() {
            log.info("Received hang up - stopping the main instance.");
            try {
                mainInstance.stop();
            } catch (Exception ex) {
                log.error("Error during stopping the main instance." + ex.getMessage());
            }
        }
    }


}
