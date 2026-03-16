package coifure.coif.notificationservice.web;

import coifure.coif.notificationservice.application.NotificationManager;
import coifure.coif.notificationservice.messaging.NotificationStore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationStore notificationStore;
    private final NotificationManager notificationManager;

    public NotificationController(NotificationStore notificationStore, NotificationManager notificationManager) {
        this.notificationStore = notificationStore;
        this.notificationManager = notificationManager;
    }

    @GetMapping
    public List<NotificationStore.NotificationMessage> list(@RequestParam(name = "recipientId", required = false) String recipientId) {
        return notificationStore.findByRecipient(recipientId);
    }

    @PostMapping
    public NotificationStore.NotificationMessage create(@Valid @RequestBody CreateNotificationRequest request) {
        return notificationManager.createAndDispatch(request.recipientId(), request.channel(), request.message());
    }

    public record CreateNotificationRequest(
            @NotBlank String recipientId,
            @NotBlank String channel,
            @NotBlank String message
    ) {
    }
}
