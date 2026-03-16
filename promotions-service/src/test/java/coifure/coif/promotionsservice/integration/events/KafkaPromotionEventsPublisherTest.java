package coifure.coif.promotionsservice.integration.events;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class KafkaPromotionEventsPublisherTest {

    @Test
    void promotionCreatedDoesNotThrowWhenKafkaSendFails() {
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        doThrow(new RuntimeException("Kafka unavailable"))
                .when(kafkaTemplate)
                .send("promotion.created", "promo-1", event());

        KafkaPromotionEventsPublisher publisher = new KafkaPromotionEventsPublisher(
                kafkaTemplate,
                new PromotionEventsProperties(
                        true,
                        new PromotionEventsProperties.Topics("promotion.created", "promotion.status.updated")
                )
        );

        assertDoesNotThrow(() -> publisher.promotionCreated(event()));
    }

    private PromotionEventPayload event() {
        return new PromotionEventPayload(
                "promo-1",
                "salon-1",
                "Ramadan",
                BigDecimal.valueOf(20),
                LocalDate.of(2026, 3, 11),
                LocalDate.of(2026, 3, 20),
                true
        );
    }
}
