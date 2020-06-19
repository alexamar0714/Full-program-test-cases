package drinkwater;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Created by A406775 on 2/01/2017.
 */
public interface IDrinkWaterService extends IPropertyResolver {

    ITracer getTracer();

    void sendEvent(Class eventClass, Method method, Object body);

    IServiceConfiguration getConfiguration();

    void start();

    void stop();

    ServiceState getState();

    String getApplicationName();

    <V> V addProperty(String key, Function<String, ? extends V> mappingFunction);



}
