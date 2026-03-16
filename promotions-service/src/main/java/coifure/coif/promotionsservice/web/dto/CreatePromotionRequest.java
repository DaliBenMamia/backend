package coifure.coif.promotionsservice.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePromotionRequest(
        @NotBlank String salonId,
        @NotBlank String title,
        String description,
        @NotNull @DecimalMin("1.00") @DecimalMax("100.00") BigDecimal discountPercent,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {
}
