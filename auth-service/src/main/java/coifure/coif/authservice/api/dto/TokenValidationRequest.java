package coifure.coif.authservice.api.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenValidationRequest(
        @NotBlank
        String token
) {
}
