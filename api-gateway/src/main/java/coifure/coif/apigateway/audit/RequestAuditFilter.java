package coifure.coif.apigateway.audit;

import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(value = "app.audit.enabled", havingValue = "true", matchIfMissing = true)
public class RequestAuditFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAuditFilter.class);
    private final GatewayRequestLogRepository logRepository;

    public RequestAuditFilter(GatewayRequestLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Instant start = Instant.now();
        exchange.getResponse().beforeCommit(() -> persistLog(exchange, start));
        return chain.filter(exchange);
    }

    private Mono<Void> persistLog(ServerWebExchange exchange, Instant start) {
        String method = exchange.getRequest().getMethod() != null
                ? exchange.getRequest().getMethod().name()
                : "UNKNOWN";
        String path = exchange.getRequest().getURI().getPath();
        Integer statusCode = exchange.getResponse().getStatusCode() != null
                ? exchange.getResponse().getStatusCode().value()
                : 500;

        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route != null ? route.getId() : "unmatched";

        GatewayRequestLog log = new GatewayRequestLog(
                method,
                path,
                statusCode,
                Duration.between(start, Instant.now()).toMillis(),
                routeId,
                Instant.now()
        );

        return logRepository.save(log)
                .doOnError(error -> LOGGER.warn("Failed to persist gateway request log", error))
                .onErrorResume(error -> Mono.empty())
                .then();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
