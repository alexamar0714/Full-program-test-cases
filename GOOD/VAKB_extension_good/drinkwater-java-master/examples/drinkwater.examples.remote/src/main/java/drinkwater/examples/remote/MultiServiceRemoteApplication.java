package drinkwater.examples.remote;

import drinkwater.ApplicationBuilder;
import drinkwater.examples.multiservice.IServiceA;
import drinkwater.examples.multiservice.IServiceB;
import drinkwater.examples.multiservice.IServiceC;
import drinkwater.examples.multiservice.IServiceD;
import drinkwater.examples.multiservice.impl.ServiceAImpl;
import drinkwater.examples.multiservice.impl.ServiceBImpl;
import drinkwater.examples.multiservice.impl.ServiceCImpl;
import drinkwater.examples.multiservice.impl.ServiceDImpl;

/**
 * Created by A406775 on 2/01/2017.
 */
public class MultiServiceRemoteApplication extends ApplicationBuilder {

    @Override
    public void configure() {
        addService("serviceD", IServiceD.class, ServiceDImpl.class);
        addService("serviceC", IServiceC.class, ServiceCImpl.class);
        addService("serviceB", IServiceB.class, ServiceBImpl.class, "serviceC", "serviceD");
        addService("serviceA", IServiceA.class, ServiceAImpl.class, "serviceB").asRest();
    }
}
