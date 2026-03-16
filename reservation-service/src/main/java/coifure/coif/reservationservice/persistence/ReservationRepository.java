package coifure.coif.reservationservice.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, String> {
    List<ReservationEntity> findAllByOrderByDateTimeAsc();

    List<ReservationEntity> findAllByUserIdOrderByDateTimeAsc(String userId);

    List<ReservationEntity> findAllByHairdresserIdOrderByDateTimeAsc(String hairdresserId);

    List<ReservationEntity> findAllByUserIdAndHairdresserIdOrderByDateTimeAsc(String userId, String hairdresserId);
}
