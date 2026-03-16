package coifure.coif.aiservice.web;

import coifure.coif.aiservice.application.AiInsightService;
import coifure.coif.aiservice.application.ChatbotService;
import coifure.coif.aiservice.web.dto.ChatRequest;
import coifure.coif.aiservice.web.dto.ChatResponse;
import coifure.coif.aiservice.web.dto.PlanningOptimizationResponse;
import coifure.coif.aiservice.web.dto.PreferencesResponse;
import coifure.coif.aiservice.web.dto.RecommendationResponse;
import coifure.coif.aiservice.web.dto.TrendsResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    private final AiInsightService aiInsightService;
    private final ChatbotService chatbotService;

    public AiController(AiInsightService aiInsightService, ChatbotService chatbotService) {
        this.aiInsightService = aiInsightService;
        this.chatbotService = chatbotService;
    }

    @GetMapping("/recommendations/{userId}")
    public RecommendationResponse recommendations(@PathVariable String userId) {
        return aiInsightService.buildRecommendations(userId);
    }

    @GetMapping("/recommendations")
    public RecommendationResponse recommendationsCompat(@RequestParam(defaultValue = "guest") String userId) {
        return aiInsightService.buildRecommendations(userId);
    }

    @GetMapping("/preferences/{userId}")
    public PreferencesResponse preferences(@PathVariable String userId) {
        return aiInsightService.buildPreferences(userId);
    }

    @GetMapping("/trends")
    public TrendsResponse trends() {
        return aiInsightService.buildTrends();
    }

    @GetMapping("/planning-optimization")
    public PlanningOptimizationResponse planningOptimization() {
        return aiInsightService.buildPlanningOptimization();
    }

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return chatbotService.reply(request);
    }
}
