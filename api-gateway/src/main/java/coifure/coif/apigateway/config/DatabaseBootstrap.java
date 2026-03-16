package coifure.coif.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(value = "app.database.bootstrap-enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseBootstrap implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseBootstrap.class);
    private final DatabaseClient databaseClient;

    public DatabaseBootstrap(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public void run(ApplicationArguments args) {
        databaseClient.sql("""
                CREATE TABLE IF NOT EXISTS gateway_request_logs (
                    id BIGSERIAL PRIMARY KEY,
                    method VARCHAR(10) NOT NULL,
                    path VARCHAR(500) NOT NULL,
                    status_code INTEGER NOT NULL,
                    duration_ms BIGINT NOT NULL,
                    route_id VARCHAR(100) NOT NULL,
                    created_at TIMESTAMPTZ NOT NULL
                )
                """)
                .fetch()
                .rowsUpdated()
                .doOnError(error -> LOGGER.error("Failed to initialize gateway_request_logs table", error))
                .onErrorResume(error -> {
                    LOGGER.warn("Continuing startup without DB bootstrap");
                    return Mono.empty();
                })
                .block();
    }
}
