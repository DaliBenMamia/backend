package coifure.coif.promotionsservice.integration.events;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PromotionEventPayload(
        String promotionId,
        String salonId,
        String title,
        BigDecimal discountPercent,
        LocalDate startDate,
        LocalDate endDate,
        boolean active
) {
}
