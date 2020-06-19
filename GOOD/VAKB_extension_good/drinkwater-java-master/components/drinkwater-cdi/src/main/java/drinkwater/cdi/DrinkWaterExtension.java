package drinkwater.cdi;

import drinkwater.ApplicationBuilder;
import drinkwater.core.DrinkWaterApplication;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;

/**
 * Created by A406775 on 27/12/2016.
 */
public class DrinkWaterExtension implements Extension {

    void addDrinkWater(@Observes AfterBeanDiscovery abdEvent, BeanManager manager) {
        abdEvent.addBean(new DrinkWaterApplicationBean());
    }

    void configureDrinkWater(@Observes AfterDeploymentValidation adv, BeanManager manager) throws Exception {

        DrinkWaterApplication dwapp =
                (DrinkWaterApplication) manager.getReference(
                        manager.resolve(manager.getBeans(DrinkWaterApplication.class)),
                        DrinkWaterApplication.class,
                        manager.createCreationalContext(null)
                );


        for (Bean<?> serviceConfigBean : manager.getBeans(ApplicationBuilder.class)) {

            ApplicationBuilder builder = (ApplicationBuilder) manager.getReference(serviceConfigBean,
                    ApplicationBuilder.class,
                    manager.createCreationalContext(serviceConfigBean));

            dwapp.addServiceBuilder(builder);

        }

        dwapp.start();

    }

    void stopDrinkWaterApplication(@Observes BeforeShutdown bsd, BeanManager mgr) throws Exception {
        DrinkWaterApplication dwapp =
                (DrinkWaterApplication) mgr.getReference(
                        mgr.resolve(mgr.getBeans(DrinkWaterApplication.class)),
                        DrinkWaterApplication.class,
                        mgr.createCreationalContext(null)
                );

        dwapp.stop();

    }

}
