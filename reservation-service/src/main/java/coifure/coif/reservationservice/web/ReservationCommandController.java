package coifure.coif.reservationservice.web;

import coifure.coif.reservationservice.integration.ExternalIdentityGateway;
import coifure.coif.reservationservice.persistence.ReservationEntity;
import coifure.coif.reservationservice.persistence.ReservationRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
public class ReservationCommandController {
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_ACCEPTED = "ACCEPTED";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String STATUS_REFUSED = "REFUSED";
    private static final String STATUS_RESCHEDULED = "RESCHEDULED";
    private static final String STATUS_COMPLETED = "COMPLETED";

    private static final Map<String, Set<String>> ALLOWED_STATUS_TRANSITIONS = Map.of(
            STATUS_PENDING, Set.of(STATUS_ACCEPTED, STATUS_CANCELLED, STATUS_REFUSED, STATUS_RESCHEDULED),
            STATUS_ACCEPTED, Set.of(STATUS_COMPLETED, STATUS_CANCELLED, STATUS_RESCHEDULED),
            STATUS_RESCHEDULED, Set.of(STATUS_ACCEPTED, STATUS_CANCELLED, STATUS_REFUSED, STATUS_RESCHEDULED),
            STATUS_CANCELLED, Set.of(),
            STATUS_REFUSED, Set.of(),
            STATUS_COMPLETED, Set.of()
    );

    private final ReservationRepository reservationRepository;
    private final ExternalIdentityGateway externalIdentityGateway;

    public ReservationCommandController(ReservationRepository reservationRepository, ExternalIdentityGateway externalIdentityGateway) {
        this.reservationRepository = reservationRepository;
        this.externalIdentityGateway = externalIdentityGateway;
    }

    @PostMapping
    public ReservationResponse createReservation(@Valid @RequestBody CreateReservationRequest request) {
        validateDateInFuture(request.dateTime());
        externalIdentityGateway.validateReferences(request.userId(), request.salonId(), request.hairdresserId());
        String reservationId = UUID.randomUUID().toString();
        ReservationEntity reservation = new ReservationEntity(
                reservationId,
                request.userId(),
                request.salonId(),
                request.hairdresserId(),
                request.serviceName(),
                request.dateTime(),
                STATUS_PENDING,
                null
        );
        return toResponse(reservationRepository.save(reservation));
    }

    @GetMapping("/{reservationId}")
    public ReservationResponse getById(@PathVariable("reservationId") String reservationId) {
        return toResponse(findReservationOrThrow(reservationId));
    }

    @GetMapping
    public List<ReservationResponse> listByClient(@RequestParam(name = "userId", required = false) String userId,
                                                  @RequestParam(name = "hairdresserId", required = false) String hairdresserId) {
        return findReservations(userId, hairdresserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/{reservationId}/cancel")
    public ReservationResponse cancel(@PathVariable("reservationId") String reservationId, @Valid @RequestBody(required = false) StatusReasonRequest request) {
        return updateStatus(reservationId, STATUS_CANCELLED, request == null ? null : request.reason());
    }

    @PatchMapping("/{reservationId}/reschedule")
    public ReservationResponse reschedule(@PathVariable("reservationId") String reservationId, @Valid @RequestBody RescheduleRequest request) {
        if (request == null || request.dateTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateTime is required");
        }
        validateDateInFuture(request.dateTime());
        ReservationEntity current = findReservationOrThrow(reservationId);
        validateStatusTransition(current.getStatus(), STATUS_RESCHEDULED);
        current.setDateTime(request.dateTime());
        current.setStatus(STATUS_RESCHEDULED);
        current.setReason(request.reason());
        return toResponse(reservationRepository.save(current));
    }

    @PatchMapping("/{reservationId}/accept")
    public ReservationResponse accept(@PathVariable("reservationId") String reservationId) {
        return updateStatus(reservationId, STATUS_ACCEPTED, null);
    }

    @PatchMapping("/{reservationId}/refuse")
    public ReservationResponse refuse(@PathVariable("reservationId") String reservationId, @Valid @RequestBody(required = false) StatusReasonRequest request) {
        return updateStatus(reservationId, STATUS_REFUSED, request == null ? null : request.reason());
    }

    @PatchMapping("/{reservationId}/complete")
    public ReservationResponse complete(@PathVariable("reservationId") String reservationId) {
        return updateStatus(reservationId, STATUS_COMPLETED, null);
    }

    private ReservationResponse updateStatus(String reservationId, String status, String reason) {
        ReservationEntity current = findReservationOrThrow(reservationId);
        validateStatusTransition(current.getStatus(), status);
        current.setStatus(status);
        current.setReason(reason);
        return toResponse(reservationRepository.save(current));
    }

    private void validateDateInFuture(LocalDateTime dateTime) {
        if (dateTime == null || !dateTime.isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateTime must be in the future");
        }
    }

    private void validateStatusTransition(String currentStatus, String nextStatus) {
        if (!ALLOWED_STATUS_TRANSITIONS.getOrDefault(currentStatus, Set.of()).contains(nextStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Invalid status transition from " + currentStatus + " to " + nextStatus
            );
        }
    }

    private List<ReservationEntity> findReservations(String userId, String hairdresserId) {
        if (userId != null && hairdresserId != null) {
            return reservationRepository.findAllByUserIdAndHairdresserIdOrderByDateTimeAsc(userId, hairdresserId);
        }
        if (userId != null) {
            return reservationRepository.findAllByUserIdOrderByDateTimeAsc(userId);
        }
        if (hairdresserId != null) {
            return reservationRepository.findAllByHairdresserIdOrderByDateTimeAsc(hairdresserId);
        }
        return reservationRepository.findAllByOrderByDateTimeAsc();
    }

    private ReservationEntity findReservationOrThrow(String reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
    }

    private ReservationResponse toResponse(ReservationEntity entity) {
        return new ReservationResponse(
                entity.getReservationId(),
                entity.getUserId(),
                entity.getSalonId(),
                entity.getHairdresserId(),
                entity.getServiceName(),
                entity.getDateTime(),
                entity.getStatus(),
                entity.getReason()
        );
    }

    public record CreateReservationRequest(
            @NotBlank @Size(max = 120) String userId,
            @NotBlank @Size(max = 120) String salonId,
            @NotBlank @Size(max = 120) String hairdresserId,
            @NotBlank @Size(max = 120) String serviceName,
            @NotNull LocalDateTime dateTime
    ) {}

    public record RescheduleRequest(@NotNull LocalDateTime dateTime, @Size(max = 255) String reason) {}

    public record StatusReasonRequest(@Size(max = 255) String reason) {}

    public record ReservationResponse(
            String reservationId,
            String userId,
            String salonId,
            String hairdresserId,
            String serviceName,
            LocalDateTime dateTime,
            String status,
            String reason
    ) {}
}
