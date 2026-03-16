package coifure.coif.notificationservice.messaging;

import coifure.coif.notificationservice.persistence.NotificationEntity;
import coifure.coif.notificationservice.persistence.NotificationRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class NotificationStore {
    private final NotificationRepository notificationRepository;

    public NotificationStore(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationMessage add(String recipientId, String channel, String message) {
        NotificationEntity entity = new NotificationEntity();
        entity.setNotificationId(UUID.randomUUID().toString());
        entity.setRecipientId(recipientId);
        entity.setChannel(channel);
        entity.setMessage(message);
        entity.setCreatedAt(Instant.now());
        NotificationEntity saved = notificationRepository.save(entity);
        return toMessage(saved);
    }

    public List<NotificationMessage> findByRecipient(String recipientId) {
        List<NotificationEntity> entities;
        if (recipientId == null || recipientId.isBlank()) {
            entities = notificationRepository.findAllByOrderByCreatedAtDesc();
        } else {
            entities = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
        }

        return entities.stream().map(this::toMessage).toList();
    }

    private NotificationMessage toMessage(NotificationEntity entity) {
        return new NotificationMessage(
                entity.getNotificationId(),
                entity.getRecipientId(),
                entity.getChannel(),
                entity.getMessage(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null
        );
    }

    public record NotificationMessage(
            String notificationId,
            String recipientId,
            String channel,
            String message,
            String createdAt
    ) {
    }
}
