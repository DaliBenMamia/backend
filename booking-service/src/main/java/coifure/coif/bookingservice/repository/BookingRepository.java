package coifure.coif.bookingservice.repository;

import coifure.coif.bookingservice.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByUserIdOrderByDateTimeAsc(String userId);
    List<Booking> findAllByOrderByDateTimeAsc();
}
