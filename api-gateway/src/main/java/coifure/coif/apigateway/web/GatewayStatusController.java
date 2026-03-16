package coifure.coif.apigateway.web;

import coifure.coif.apigateway.audit.GatewayRequestLog;
import coifure.coif.apigateway.audit.GatewayRequestLogRepository;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/gateway")
public class GatewayStatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayStatusController.class);
    private final GatewayRequestLogRepository logRepository;

    public GatewayStatusController(GatewayRequestLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @GetMapping("/health")
    public Mono<Map<String, Object>> health() {
        return Mono.just(Map.of(
                "service", "api-gateway",
                "status", "UP",
                "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping("/logs")
    public Flux<GatewayRequestLog> lastLogs() {
        return logRepository.findTop50ByOrderByCreatedAtDesc()
                .doOnError(error -> LOGGER.warn("Unable to fetch gateway logs from database", error))
                .onErrorResume(error -> Flux.empty());
    }

    @RequestMapping("/fallback/{service}")
    public Mono<ResponseEntity<Map<String, Object>>> fallback(@PathVariable String service) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "service", "api-gateway",
                "status", "UNAVAILABLE",
                "targetService", service,
                "message", "Downstream service is unavailable",
                "timestamp", Instant.now().toString()
        )));
    }
}
