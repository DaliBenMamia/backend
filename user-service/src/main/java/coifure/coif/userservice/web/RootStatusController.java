package coifure.coif.userservice.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController

public class RootStatusController {    @GetMapping("/")
public Map<String, String> rootStatus() {
    return Map.of("service", "user-service", "status", "UP");
}
}


