package coifure.coif.aiservice.web;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")

public class HealthController {
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "ai-service", "status", "UP");
    }
}
