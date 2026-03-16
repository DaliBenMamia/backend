package coifure.coif.bookingservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class ExternalServicesClient {

    private final RestClient restClient;
    private final String userServiceBaseUrl;
    private final String salonServiceBaseUrl;
    private final String authServiceBaseUrl;
    private final String reservationServiceBaseUrl;
    private final boolean authValidationEnabled;
    private final boolean userValidationEnabled;
    private final boolean salonValidationEnabled;
    private final boolean reservationForwardingEnabled;

    public ExternalServicesClient(
            RestClient.Builder restClientBuilder,
            @Value("${user.service.base-url:http://localhost:8893}") String userServiceBaseUrl,
            @Value("${salon.service.base-url:http://localhost:8894}") String salonServiceBaseUrl,
            @Value("${auth.service.base-url:http://localhost:8220}") String authServiceBaseUrl,
            @Value("${reservation.service.base-url:http://localhost:8896}") String reservationServiceBaseUrl,
            @Value("${external.auth.validation.enabled:false}") boolean authValidationEnabled,
            @Value("${external.user.validation.enabled:false}") boolean userValidationEnabled,
            @Value("${external.salon.validation.enabled:false}") boolean salonValidationEnabled,
            @Value("${external.reservation.forwarding.enabled:false}") boolean reservationForwardingEnabled
    ) {
        this.restClient = restClientBuilder.build();
        this.userServiceBaseUrl = userServiceBaseUrl;
        this.salonServiceBaseUrl = salonServiceBaseUrl;
        this.authServiceBaseUrl = authServiceBaseUrl;
        this.reservationServiceBaseUrl = reservationServiceBaseUrl;
        this.authValidationEnabled = authValidationEnabled;
        this.userValidationEnabled = userValidationEnabled;
        this.salonValidationEnabled = salonValidationEnabled;
        this.reservationForwardingEnabled = reservationForwardingEnabled;
    }

    public void assertUserExists(String userId) {
        if (!userValidationEnabled) {
            return;
        }
        assertResourceExists(userServiceBaseUrl, "/api/users/{id}", userId, "User");
    }

    public void assertSalonExists(String salonId) {
        if (!salonValidationEnabled) {
            return;
        }
        assertResourceExists(salonServiceBaseUrl, "/api/salons/{id}", salonId, "Salon");
    }

    public AuthValidation validateToken(String token) {
        if (!authValidationEnabled) {
            return new AuthValidation(true, null, null, null);
        }
        String url = authServiceBaseUrl + "/api/auth/validate";
        try {
            AuthValidation response = restClient.post()
                    .uri(url)
                    .body(Map.of("token", token))
                    .retrieve()
                    .body(AuthValidation.class);

            if (response == null || !response.valid()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
            }
            return response;
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Auth service error (" + e.getStatusCode().value() + ")"
            );
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Auth service unavailable");
        }
    }

    public ReservationForwardResponse createReservation(CreateReservationForwardRequest request) {
        if (!reservationForwardingEnabled) {
            return new ReservationForwardResponse(null, "MOCKED");
        }

        String url = reservationServiceBaseUrl + "/api/reservations";
        try {
            ReservationForwardResponse response = restClient.post()
                    .uri(url)
                    .body(request)
                    .retrieve()
                    .body(ReservationForwardResponse.class);

            if (response == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Reservation service returned no body");
            }
            return response;
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Reservation service error (" + e.getStatusCode().value() + ")"
            );
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Reservation service unavailable");
        }
    }

    private void assertResourceExists(String baseUrl, String path, String id, String resourceName) {
        String url = baseUrl + path;
        try {
            restClient.get()
                    .uri(url, id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, resourceName + " not found: " + id);
            }
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    resourceName + " service error (" + e.getStatusCode().value() + ")"
            );
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, resourceName + " service unavailable");
        }
    }

    public record AuthValidation(boolean valid, String userId, String email, String role) {
    }

    public record CreateReservationForwardRequest(
            String userId,
            String salonId,
            String hairdresserId,
            String serviceId,
            LocalDateTime reservationDate,
            BigDecimal amount,
            String paymentMethod
    ) {
    }

    public record ReservationForwardResponse(String id, String status) {
    }
}
