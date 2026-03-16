package coifure.coif.bookingservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "bookable_services")
public class BookableService {

    @Id
    @Column(nullable = false, updatable = false, length = 64)
    private String serviceId;

    @Column(nullable = false, length = 120)
    private String salonId;

    @Column(nullable = false, length = 120)
    private String hairdresserId;

    @Column(nullable = false, length = 120)
    private String serviceName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String defaultPaymentMethod;

    public BookableService() {
    }

    public BookableService(String serviceId,
                           String salonId,
                           String hairdresserId,
                           String serviceName,
                           BigDecimal amount,
                           String defaultPaymentMethod) {
        this.serviceId = serviceId;
        this.salonId = salonId;
        this.hairdresserId = hairdresserId;
        this.serviceName = serviceName;
        this.amount = amount;
        this.defaultPaymentMethod = defaultPaymentMethod;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getSalonId() {
        return salonId;
    }

    public String getHairdresserId() {
        return hairdresserId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDefaultPaymentMethod() {
        return defaultPaymentMethod;
    }
}
