package coifure.coif.promotionsservice.integration.events;

public interface PromotionEventsPublisher {
    void promotionCreated(PromotionEventPayload payload);
    void promotionStatusUpdated(PromotionEventPayload payload);
}
