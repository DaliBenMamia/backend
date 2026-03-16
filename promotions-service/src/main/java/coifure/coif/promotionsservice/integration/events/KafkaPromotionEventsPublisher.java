package coifure.coif.promotionsservice.integration.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.events.enabled", havingValue = "true")
public class KafkaPromotionEventsPublisher implements PromotionEventsPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaPromotionEventsPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String createdTopic;
    private final String statusTopic;

    public KafkaPromotionEventsPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                         PromotionEventsProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.createdTopic = properties.topics().promotionCreated();
        this.statusTopic = properties.topics().promotionStatusUpdated();
    }

    @Override
    public void promotionCreated(PromotionEventPayload payload) {
        sendSafely(createdTopic, payload);
    }

    @Override
    public void promotionStatusUpdated(PromotionEventPayload payload) {
        sendSafely(statusTopic, payload);
    }

    private void sendSafely(String topic, PromotionEventPayload payload) {
        try {
            kafkaTemplate.send(topic, payload.promotionId(), payload);
        } catch (RuntimeException exception) {
            log.warn("Failed to publish promotion event to topic '{}': {}", topic, exception.getMessage());
        }
    }
}
