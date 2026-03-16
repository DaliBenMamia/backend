package coifure.coif.authservice.api.dto;

import java.time.Instant;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        String userId,
        String nom,
        String prenom,
        String email,
        String telephone,
        String role
) {
}
