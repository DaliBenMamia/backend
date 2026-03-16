package coifure.coif.userservice.web;

public record UpdateProfileRequest(String fullName, String email, String speciality, String phone) {
}
