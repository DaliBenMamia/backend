package coifure.coif.notificationservice.application;

import coifure.coif.notificationservice.delivery.NotificationDeliveryClient;
import coifure.coif.notificationservice.messaging.NotificationStore;
import org.springframework.stereotype.Service;

@Service
public class NotificationManager {
    private final NotificationStore notificationStore;
    private final NotificationDeliveryClient deliveryClient;

    public NotificationManager(NotificationStore notificationStore, NotificationDeliveryClient deliveryClient) {
        this.notificationStore = notificationStore;
        this.deliveryClient = deliveryClient;
    }

    public NotificationStore.NotificationMessage createAndDispatch(String recipientId, String channel, String message) {
        NotificationStore.NotificationMessage saved = notificationStore.add(recipientId, channel, message);
        deliveryClient.dispatch(saved);
        return saved;
    }
}
