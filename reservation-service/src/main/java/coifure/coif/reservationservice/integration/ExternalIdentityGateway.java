package coifure.coif.reservationservice.integration;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ExternalIdentityGateway {
    private final RestClient restClient;
    private final ExternalServicesProperties properties;

    public ExternalIdentityGateway(RestClient externalServicesRestClient, ExternalServicesProperties properties) {
        this.restClient = externalServicesRestClient;
        this.properties = properties;
    }

    public void validateReferences(String userId, String salonId, String hairdresserId) {
        if (!properties.enabled()) {
            return;
        }
        checkExists("userId", userId, properties.userByIdUrlTemplate());
        checkExists("salonId", salonId, properties.salonByIdUrlTemplate());
        checkExists("hairdresserId", hairdresserId, properties.hairdresserByIdUrlTemplate());
    }

    private void checkExists(String fieldName, String id, String urlTemplate) {
        if (urlTemplate == null || urlTemplate.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Missing URL template for " + fieldName + " validation"
            );
        }
        try {
            restClient.get()
                    .uri(urlTemplate, id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " not found: " + id);
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Failed to validate " + fieldName + " via external service"
            );
        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "External service unavailable for " + fieldName + " validation"
            );
        }
    }
}
