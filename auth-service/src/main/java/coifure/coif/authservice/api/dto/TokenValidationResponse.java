package coifure.coif.authservice.api.dto;

public record TokenValidationResponse(
        boolean valid,
        String userId,
        String email,
        String role,
        String nom,
        String prenom,
        String telephone
) {
}
