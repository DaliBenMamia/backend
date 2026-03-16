package coifure.coif.userservice.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
@RestController
@RequestMapping("/api/users")

public class HealthController {
    @GetMapping("/health")
public Map<String, String> health() {
    return Map.of("service", "user-service", "status", "UP");
}
}

