package coifure.coif.bookingservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @Column(nullable = false, updatable = false, length = 36)
    private String bookingId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String salonId;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private String hairdresserId;

    @Column(nullable = false)
    private String serviceId;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(length = 64)
    private String reservationId;

    public Booking() {
    }

    public Booking(String bookingId,
                   String userId,
                   String salonId,
                   String serviceName,
                   String hairdresserId,
                   String serviceId,
                   LocalDateTime dateTime,
                   BigDecimal amount,
                   String paymentMethod,
                   BookingStatus status,
                   String reservationId) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.salonId = salonId;
        this.serviceName = serviceName;
        this.hairdresserId = hairdresserId;
        this.serviceId = serviceId;
        this.dateTime = dateTime;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.reservationId = reservationId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSalonId() {
        return salonId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getHairdresserId() {
        return hairdresserId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
}
