package coifure.coif.aiservice.web.dto;

import java.util.List;

public record PreferencesResponse(
        String userId,
        List<String> favoriteServices,
        List<String> usualTimeSlots,
        List<String> mostVisitedSalons,
        List<String> recurringPreferences
) {
}
