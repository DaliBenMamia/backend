package coifure.coif.aiservice.web.dto;

import java.util.List;

public record PlanningOptimizationResponse(
        List<String> saturatedHours,
        List<String> underUsedSlots,
        List<String> optimizationSuggestions
) {
}
