package coifure.coif.notificationservice.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByRecipientIdOrderByCreatedAtDesc(String recipientId);

    List<NotificationEntity> findAllByOrderByCreatedAtDesc();
}
