package coifure.coif.bookingservice.service;

import coifure.coif.bookingservice.domain.BookableService;
import coifure.coif.bookingservice.repository.BookableServiceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class BookableServiceCatalogInitializer {

    private final BookableServiceRepository bookableServiceRepository;

    public BookableServiceCatalogInitializer(BookableServiceRepository bookableServiceRepository) {
        this.bookableServiceRepository = bookableServiceRepository;
    }

    @PostConstruct
    void seedDefaults() {
        if (bookableServiceRepository.count() > 0) {
            return;
        }
        bookableServiceRepository.saveAll(List.of(
                new BookableService("service-2", "salon-2", "hairdresser-5", "Coupe premium", new BigDecimal("45.00"), "ONLINE"),
                new BookableService("5", "salon-2", "hairdresser-5", "Coupe premium", new BigDecimal("45.00"), "ONLINE")
        ));
    }
}
