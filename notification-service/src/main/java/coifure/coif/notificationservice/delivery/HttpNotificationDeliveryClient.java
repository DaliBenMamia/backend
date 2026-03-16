package coifure.coif.notificationservice.delivery;

import coifure.coif.notificationservice.messaging.NotificationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "app.delivery.enabled", havingValue = "true")
public class HttpNotificationDeliveryClient implements NotificationDeliveryClient {
    private static final Logger log = LoggerFactory.getLogger(HttpNotificationDeliveryClient.class);

    private final RestClient restClient;
    private final String endpointPath;

    public HttpNotificationDeliveryClient(
            RestClient.Builder restClientBuilder,
            @Value("${app.delivery.base-url}") String baseUrl,
            @Value("${app.delivery.endpoint:/api/deliveries}") String endpointPath
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.endpointPath = endpointPath;
    }

    @Override
    public void dispatch(NotificationStore.NotificationMessage message) {
        DeliveryRequest payload = new DeliveryRequest(
                message.notificationId(),
                message.recipientId(),
                message.channel(),
                message.message(),
                message.createdAt()
        );

        try {
            restClient.post()
                    .uri(endpointPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            log.warn("Notification delivery failed for notificationId={} endpoint={}", message.notificationId(), endpointPath, ex);
        }
    }

    public record DeliveryRequest(
            String notificationId,
            String recipientId,
            String channel,
            String message,
            String createdAt
    ) {
    }
}
