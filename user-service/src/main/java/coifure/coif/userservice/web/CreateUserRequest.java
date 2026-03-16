package coifure.coif.userservice.web;

public record CreateUserRequest(
        String userId,
        String fullName,
        String email,
        String role,
        String speciality,
        String phone
) {
}
