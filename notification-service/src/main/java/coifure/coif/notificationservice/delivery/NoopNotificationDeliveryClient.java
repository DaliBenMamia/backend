package coifure.coif.notificationservice.delivery;

import coifure.coif.notificationservice.messaging.NotificationStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.delivery.enabled", havingValue = "false", matchIfMissing = true)
public class NoopNotificationDeliveryClient implements NotificationDeliveryClient {
    @Override
    public void dispatch(NotificationStore.NotificationMessage message) {
        // Delivery disabled in current profile/environment.
    }
}
