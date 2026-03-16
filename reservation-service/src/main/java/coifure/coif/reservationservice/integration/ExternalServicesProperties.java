package coifure.coif.reservationservice.integration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.services")
public record ExternalServicesProperties(
        boolean enabled,
        int connectTimeoutMs,
        int readTimeoutMs,
        String userByIdUrlTemplate,
        String salonByIdUrlTemplate,
        String hairdresserByIdUrlTemplate
) {
}
