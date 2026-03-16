package coifure.coif.apigateway.audit;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GatewayRequestLogRepository extends ReactiveCrudRepository<GatewayRequestLog, Long> {

    Flux<GatewayRequestLog> findTop50ByOrderByCreatedAtDesc();
}
