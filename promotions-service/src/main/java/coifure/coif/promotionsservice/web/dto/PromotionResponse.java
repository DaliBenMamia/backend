package coifure.coif.promotionsservice.web.dto;

import coifure.coif.promotionsservice.domain.Promotion;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PromotionResponse(
        String id,
        String salonId,
        String title,
        String description,
        BigDecimal discountPercent,
        LocalDate startDate,
        LocalDate endDate,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static PromotionResponse from(Promotion promotion) {
        return new PromotionResponse(
                promotion.getId(),
                promotion.getSalonId(),
                promotion.getTitle(),
                promotion.getDescription(),
                promotion.getDiscountPercent(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.isActive(),
                promotion.getCreatedAt(),
                promotion.getUpdatedAt()
        );
    }
}
