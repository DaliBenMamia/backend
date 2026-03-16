package coifure.coif.promotionsservice.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootStatusController {

    @GetMapping("/")
    public Map<String, String> rootStatus() {
        return Map.of("service", "promotion-service", "status", "UP");
    }
}
