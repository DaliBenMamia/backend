package coifure.coif.promotionsservice.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/promotions")
public class HealthController {
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "promotion-service", "status", "UP");
    }
}

