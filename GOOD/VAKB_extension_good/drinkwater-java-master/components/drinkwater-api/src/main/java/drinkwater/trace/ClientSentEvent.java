package drinkwater.trace;

import java.time.Instant;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ClientSentEvent extends BaseEvent {
    public ClientSentEvent(Instant instant, String correlationId, String description,
                           String applicationName, String serviceName,Payload payload) {
        super(instant, "CLS", correlationId, description, applicationName, serviceName,payload);
    }
}
