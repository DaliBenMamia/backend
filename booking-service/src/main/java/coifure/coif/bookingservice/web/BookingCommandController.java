package coifure.coif.bookingservice.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import coifure.coif.bookingservice.domain.Booking;
import coifure.coif.bookingservice.service.BookingService;
import coifure.coif.bookingservice.service.BookableServiceCatalog;
import coifure.coif.bookingservice.service.ExternalServicesClient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingCommandController {

    private final BookingService bookingService;
    private final BookableServiceCatalog bookableServiceCatalog;
    private final ExternalServicesClient externalServicesClient;
    private final boolean authValidationEnabled;

    public BookingCommandController(
            BookingService bookingService,
            BookableServiceCatalog bookableServiceCatalog,
            ExternalServicesClient externalServicesClient,
            @Value("${external.auth.validation.enabled:false}") boolean authValidationEnabled
    ) {
        this.bookingService = bookingService;
        this.bookableServiceCatalog = bookableServiceCatalog;
        this.externalServicesClient = externalServicesClient;
        this.authValidationEnabled = authValidationEnabled;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody CreateBookingRequest request
    ) {
        ExternalServicesClient.AuthValidation auth = externalServicesClient.validateToken(extractBearerToken(authorization));
        if (authValidationEnabled && (auth.userId() == null || !auth.userId().equals(request.userId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token user mismatch");
        }

        BookableServiceCatalog.ResolvedBookingDetails resolved = bookableServiceCatalog.resolve(
                request.serviceId(),
                request.salonId(),
                request.hairdresserId(),
                request.serviceName(),
                request.amount(),
                request.paymentMethod()
        );

        Booking booking = bookingService.createBooking(
                request.userId(),
                resolved.salonId(),
                resolved.hairdresserId(),
                request.serviceId(),
                resolved.serviceName(),
                request.dateTime(),
                resolved.amount(),
                resolved.paymentMethod()
        );
        return BookingResponse.from(booking);
    }

    @GetMapping
    public List<BookingResponse> list(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestParam(name = "userId", required = false) String userId
    ) {
        ExternalServicesClient.AuthValidation auth = externalServicesClient.validateToken(extractBearerToken(authorization));
        if (!authValidationEnabled) {
            String effectiveUserId = userId;
            return bookingService.listBookings(effectiveUserId).stream()
                    .map(BookingResponse::from)
                    .toList();
        }

        String effectiveUserId = auth.userId();
        if (effectiveUserId == null || effectiveUserId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing user in token");
        }
        if (userId != null && !userId.equals(effectiveUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access bookings of another user");
        }

        return bookingService.listBookings(effectiveUserId).stream()
                .map(BookingResponse::from)
                .toList();
    }

    private String extractBearerToken(String authorization) {
        if (!authValidationEnabled) {
            return "";
        }
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Bearer token");
        }
        String token = authorization.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Bearer token");
        }
        return token;
    }

    public static final class CreateBookingRequest {

        @NotBlank
        private final String userId;

        private final String salonId;

        private final String serviceName;

        private final String hairdresserId;

        @NotBlank
        private final String serviceId;

        @NotNull
        @Future
        private final LocalDateTime dateTime;

        @DecimalMin("0.0")
        private final BigDecimal amount;

        private final String paymentMethod;

        @JsonCreator
        public CreateBookingRequest(
                @JsonProperty("userId") String userId,
                @JsonProperty("clientId") String clientId,
                @JsonProperty("salonId") String salonId,
                @JsonProperty("serviceId") String serviceId,
                @JsonProperty("hairdresserId") String hairdresserId,
                @JsonProperty("serviceName") String serviceName,
                @JsonProperty("serviceLabel") String serviceLabel,
                @JsonProperty("dateTime") LocalDateTime dateTime,
                @JsonProperty("bookingDate") LocalDateTime bookingDate,
                @JsonProperty("amount") BigDecimal amount,
                @JsonProperty("paymentMethod") String paymentMethod
        ) {
            this.userId = firstNonBlank(userId, clientId);
            this.salonId = salonId;
            this.hairdresserId = hairdresserId;
            this.serviceId = serviceId;
            this.serviceName = firstNonBlank(serviceName, serviceLabel, serviceId);
            this.dateTime = dateTime != null ? dateTime : bookingDate;
            this.amount = amount;
            this.paymentMethod = paymentMethod;
        }

        public String userId() {
            return userId;
        }

        public String salonId() {
            return salonId;
        }

        public String serviceName() {
            return serviceName;
        }

        public String hairdresserId() {
            return hairdresserId;
        }

        public String serviceId() {
            return serviceId;
        }

        public LocalDateTime dateTime() {
            return dateTime;
        }

        public BigDecimal amount() {
            return amount;
        }

        public String paymentMethod() {
            return paymentMethod;
        }

        private static String firstNonBlank(String... values) {
            for (String value : values) {
                if (value != null && !value.isBlank()) {
                    return value;
                }
            }
            return null;
        }
    }

    public record BookingResponse(
            String bookingId,
            String userId,
            String salonId,
            String serviceName,
            LocalDateTime dateTime,
            String status,
            String hairdresserId,
            String serviceId,
            BigDecimal amount,
            String paymentMethod,
            String reservationId
    ) {
        static BookingResponse from(Booking booking) {
            return new BookingResponse(
                    booking.getBookingId(),
                    booking.getUserId(),
                    booking.getSalonId(),
                    booking.getServiceName(),
                    booking.getDateTime(),
                    booking.getStatus().name(),
                    booking.getHairdresserId(),
                    booking.getServiceId(),
                    booking.getAmount(),
                    booking.getPaymentMethod(),
                    booking.getReservationId()
            );
        }
    }
}
