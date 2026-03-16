package coifure.coif.bookingservice.repository;

import coifure.coif.bookingservice.domain.BookableService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookableServiceRepository extends JpaRepository<BookableService, String> {
}
