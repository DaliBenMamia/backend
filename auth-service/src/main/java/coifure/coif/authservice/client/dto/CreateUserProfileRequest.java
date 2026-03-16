package coifure.coif.authservice.client.dto;

public record CreateUserProfileRequest(
        String userId,
        String email,
        String role
) {
}
