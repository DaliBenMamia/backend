package coifure.coif.aiservice.web.dto;

import java.util.List;

public record RecommendationResponse(
        String userId,
        List<RecommendationItem> recommendedServices,
        List<RecommendationItem> recommendedSalons,
        List<RecommendationItem> recommendedHairdressers
) {
    public record RecommendationItem(String id, String name, String reason) {
    }
}
