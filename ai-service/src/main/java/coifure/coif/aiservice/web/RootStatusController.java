package coifure.coif.aiservice.web;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController

public class RootStatusController {
    @GetMapping("/")
    public Map<String, String> rootStatus() {
        return Map.of("service", "ai-service", "status", "UP");
    }
}
