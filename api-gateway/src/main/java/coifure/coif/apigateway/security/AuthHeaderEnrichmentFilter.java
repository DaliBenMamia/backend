package coifure.coif.apigateway.security;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(value = "app.auth-propagation.enabled", havingValue = "true", matchIfMissing = true)
public class AuthHeaderEnrichmentFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;
    private final String authServiceBaseUrl;

    public AuthHeaderEnrichmentFilter(
            WebClient.Builder webClientBuilder,
            @Value("${app.auth-service-url:http://localhost:8220}") String authServiceBaseUrl
    ) {
        this.webClient = webClientBuilder.build();
        this.authServiceBaseUrl = authServiceBaseUrl;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/api/auth/") || path.startsWith("/actuator/") || "/".equals(path)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authorization.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            return unauthorized(exchange, "Invalid bearer token");
        }

        return webClient.post()
                .uri(authServiceBaseUrl + "/api/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("token", token))
                .retrieve()
                .bodyToMono(TokenValidationPayload.class)
                .flatMap(validation -> {
                    if (validation == null || !validation.valid() || validation.userId() == null || validation.role() == null) {
                        return unauthorized(exchange, "Invalid token");
                    }

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-Viewer-Id", validation.userId())
                            .header("X-Viewer-Role", validation.role())
                            .header("X-Auth-User-Id", validation.userId())
                            .header("X-Auth-User-Role", validation.role())
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .onErrorResume(error -> unauthorized(exchange, "Authentication service unavailable"));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = ("{\"message\":\"" + message + "\"}").getBytes();
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
    }

    @Override
    public int getOrder() {
        return -5;
    }

    private record TokenValidationPayload(
            boolean valid,
            String userId,
            String email,
            String role,
            String nom,
            String prenom,
            String telephone
    ) {
    }
}
