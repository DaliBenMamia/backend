package coifure.coif.promotionsservice.integration.events;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.events.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpPromotionEventsPublisher implements PromotionEventsPublisher {

    @Override
    public void promotionCreated(PromotionEventPayload payload) {
        // Intentionally no-op when event integration is disabled.
    }

    @Override
    public void promotionStatusUpdated(PromotionEventPayload payload) {
        // Intentionally no-op when event integration is disabled.
    }
}
