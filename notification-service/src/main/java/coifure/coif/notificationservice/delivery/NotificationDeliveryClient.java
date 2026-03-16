package coifure.coif.notificationservice.delivery;

import coifure.coif.notificationservice.messaging.NotificationStore;

public interface NotificationDeliveryClient {
    void dispatch(NotificationStore.NotificationMessage message);
}
