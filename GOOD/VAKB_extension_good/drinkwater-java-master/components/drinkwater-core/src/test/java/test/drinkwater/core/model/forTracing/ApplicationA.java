package test.drinkwater.core.model.forTracing;

import drinkwater.ApplicationBuilder;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ApplicationA extends ApplicationBuilder {

    private boolean applicationTracing;
    private boolean useTracing;
    private boolean useTracingOnlyOnEntry;
    private Class eventLoggerClass;

    public ApplicationA(boolean applicationTracing, boolean useTracing, boolean useTracingOnlyOnEntry, Class eventLoggerClass) {
        this.useTracing = useTracing;
        this.useTracingOnlyOnEntry = useTracingOnlyOnEntry;
        this.eventLoggerClass = eventLoggerClass;
        this.applicationTracing = applicationTracing;
    }

    @Override
    public void configure() {

        useEventLogger(eventLoggerClass);
        useTracing(applicationTracing);

        boolean traced = useTracing;
        boolean traceb = useTracing;
        boolean tracea = useTracing;

        if (useTracingOnlyOnEntry) {
            traced = false;
            traceb = false;
            tracea = true;
        }
        addService("serviceD", IServiceD.class, ServiceDImpl.class)
                .useTracing(traced);
        addService("serviceB", IServiceB.class).addInitialProperty("drinkwater.rest.port", 8888)
                .useTracing(traceb).asRemote();
        addService("serviceA", IServiceA.class, new ServiceAImpl(), "serviceD", "serviceB")
                .useTracing(tracea).addInitialProperty("drinkwater.rest.port", 7777).asRest();


    }
}
