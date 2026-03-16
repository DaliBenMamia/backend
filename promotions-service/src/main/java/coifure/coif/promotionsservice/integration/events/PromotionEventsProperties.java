package coifure.coif.promotionsservice.integration.events;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.events")
public record PromotionEventsProperties(
        boolean enabled,
        Topics topics
) {
    public record Topics(
            String promotionCreated,
            String promotionStatusUpdated
    ) {
    }
}
