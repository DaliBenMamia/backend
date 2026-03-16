package coifure.coif.userservice.web;

import java.util.List;

public record UserProfile(
        String userId,
        String fullName,
        String email,
        String role,
        String status,
        String speciality,
        String phone,
        List<AvailabilitySlot> availabilities
) {
}
