package coifure.coif.promotionsservice.web.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePromotionStatusRequest(@NotNull Boolean active) {
}
