package coifure.coif.reservationservice.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class ReservationEntity {
    @Id
    @Column(name = "reservation_id", nullable = false, length = 40)
    private String reservationId;

    @Column(name = "user_id", nullable = false, length = 120)
    private String userId;

    @Column(name = "salon_id", nullable = false, length = 120)
    private String salonId;

    @Column(name = "hairdresser_id", nullable = false, length = 120)
    private String hairdresserId;

    @Column(name = "service_name", nullable = false, length = 120)
    private String serviceName;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "reason", length = 255)
    private String reason;

    public ReservationEntity() {
    }

    public ReservationEntity(
            String reservationId,
            String userId,
            String salonId,
            String hairdresserId,
            String serviceName,
            LocalDateTime dateTime,
            String status,
            String reason
    ) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.salonId = salonId;
        this.hairdresserId = hairdresserId;
        this.serviceName = serviceName;
        this.dateTime = dateTime;
        this.status = status;
        this.reason = reason;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSalonId() {
        return salonId;
    }

    public void setSalonId(String salonId) {
        this.salonId = salonId;
    }

    public String getHairdresserId() {
        return hairdresserId;
    }

    public void setHairdresserId(String hairdresserId) {
        this.hairdresserId = hairdresserId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
