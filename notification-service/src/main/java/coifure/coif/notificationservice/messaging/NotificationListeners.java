package coifure.coif.notificationservice.messaging;

import coifure.coif.notificationservice.application.NotificationManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class NotificationListeners {
    private static final Logger log = LoggerFactory.getLogger(NotificationListeners.class);

    private final NotificationManager notificationManager;
    private final ObjectMapper objectMapper;

    public NotificationListeners(NotificationManager notificationManager, ObjectMapper objectMapper) {
        this.notificationManager = notificationManager;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "booking.created", groupId = "notification-service")
    public void onBookingCreated(String payload) {
        JsonNode event = toJson(payload);
        String recipientId = read(event, "userId", "unknown-user");
        String salonId = read(event, "salonId", "unknown-salon");
        notificationManager.createAndDispatch(
                recipientId,
                "BOOKING",
                "Votre reservation de salon " + salonId + " a ete creee"
        );
    }

    @KafkaListener(topics = "reservation.created", groupId = "notification-service")
    public void onReservationCreated(String payload) {
        JsonNode event = toJson(payload);
        String recipientId = read(event, "userId", "unknown-user");
        String reservationId = read(event, "reservationId", "unknown-reservation");
        notificationManager.createAndDispatch(
                recipientId,
                "RESERVATION",
                "Votre rendez-vous " + reservationId + " est confirme"
        );
    }

    @KafkaListener(topics = "promotion.created", groupId = "notification-service")
    public void onPromotionCreated(String payload) {
        JsonNode event = toJson(payload);
        String salonId = read(event, "salonId", "unknown-salon");
        notificationManager.createAndDispatch(
                salonId,
                "PROMOTION",
                "Nouvelle promotion disponible pour le salon " + salonId
        );
    }

    private JsonNode toJson(String payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (Exception ex) {
            log.warn("Invalid Kafka payload received: {}", payload, ex);
            return objectMapper.createObjectNode();
        }
    }

    private String read(JsonNode event, String field, String fallback) {
        JsonNode value = event.get(field);
        if (value == null || value.isNull()) {
            return fallback;
        }
        String text = value.asText();
        return (text == null || text.isBlank()) ? fallback : text;
    }
}
