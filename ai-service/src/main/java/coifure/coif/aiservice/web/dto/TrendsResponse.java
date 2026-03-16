package coifure.coif.aiservice.web.dto;

import java.util.List;

public record TrendsResponse(
        List<String> mostRequestedServices,
        List<String> popularSalons,
        List<String> mostRequestedTimeSlots,
        List<String> generalTrends
) {
}
