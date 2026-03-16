package coifure.coif.bookingservice.service;

import coifure.coif.bookingservice.domain.BookableService;
import coifure.coif.bookingservice.repository.BookableServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class BookableServiceCatalog {

    private static final String DEFAULT_PAYMENT_METHOD = "ONLINE";

    private final BookableServiceRepository bookableServiceRepository;

    public BookableServiceCatalog(BookableServiceRepository bookableServiceRepository) {
        this.bookableServiceRepository = bookableServiceRepository;
    }

    public ResolvedBookingDetails resolve(String serviceId,
                                          String salonId,
                                          String hairdresserId,
                                          String serviceName,
                                          BigDecimal amount,
                                          String paymentMethod) {
        BookableService bookableService = bookableServiceRepository.findById(serviceId).orElse(null);

        String effectiveSalonId = firstNonBlank(salonId, bookableService == null ? null : bookableService.getSalonId());
        String effectiveHairdresserId = firstNonBlank(hairdresserId, bookableService == null ? null : bookableService.getHairdresserId());
        String effectiveServiceName = firstNonBlank(serviceName,
                bookableService == null ? null : bookableService.getServiceName(),
                serviceId);
        BigDecimal effectiveAmount = amount != null ? amount : bookableService == null ? null : bookableService.getAmount();
        String effectivePaymentMethod = firstNonBlank(paymentMethod,
                bookableService == null ? null : bookableService.getDefaultPaymentMethod(),
                DEFAULT_PAYMENT_METHOD);

        if (isBlank(effectiveSalonId) || isBlank(effectiveHairdresserId) || effectiveAmount == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unknown serviceId or incomplete booking data. Provide salonId, hairdresserId and amount, or register the service in bookable_services."
            );
        }

        return new ResolvedBookingDetails(
                effectiveSalonId,
                effectiveHairdresserId,
                effectiveServiceName,
                effectiveAmount,
                effectivePaymentMethod
        );
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    public record ResolvedBookingDetails(
            String salonId,
            String hairdresserId,
            String serviceName,
            BigDecimal amount,
            String paymentMethod
    ) {
    }
}
