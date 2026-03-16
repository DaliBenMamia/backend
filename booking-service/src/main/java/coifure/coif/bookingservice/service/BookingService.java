package coifure.coif.bookingservice.service;

import coifure.coif.bookingservice.domain.Booking;
import coifure.coif.bookingservice.domain.BookingStatus;
import coifure.coif.bookingservice.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ExternalServicesClient externalServicesClient;

    public BookingService(BookingRepository bookingRepository, ExternalServicesClient externalServicesClient) {
        this.bookingRepository = bookingRepository;
        this.externalServicesClient = externalServicesClient;
    }

    public Booking createBooking(String userId,
                                 String salonId,
                                 String hairdresserId,
                                 String serviceId,
                                 String serviceName,
                                 LocalDateTime dateTime,
                                 BigDecimal amount,
                                 String paymentMethod) {
        externalServicesClient.assertUserExists(userId);
        externalServicesClient.assertSalonExists(salonId);

        Booking booking = new Booking(
                UUID.randomUUID().toString(),
                userId,
                salonId,
                serviceName,
                hairdresserId,
                serviceId,
                dateTime,
                amount,
                paymentMethod,
                BookingStatus.CREATED,
                null
        );
        Booking savedBooking = bookingRepository.save(booking);

        ExternalServicesClient.ReservationForwardResponse reservationResponse = externalServicesClient.createReservation(
                new ExternalServicesClient.CreateReservationForwardRequest(
                        userId,
                        salonId,
                        hairdresserId,
                        serviceId,
                        dateTime,
                        amount,
                        paymentMethod
                )
        );

        savedBooking.setReservationId(reservationResponse.id());
        savedBooking.setStatus("MOCKED".equalsIgnoreCase(reservationResponse.status())
                ? BookingStatus.CREATED
                : BookingStatus.FORWARDED_TO_RESERVATION);

        return bookingRepository.save(savedBooking);
    }

    public List<Booking> listBookings(String userId) {
        if (userId == null || userId.isBlank()) {
            return bookingRepository.findAllByOrderByDateTimeAsc();
        }
        return bookingRepository.findByUserIdOrderByDateTimeAsc(userId);
    }
}
