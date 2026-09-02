package com.example.notification_system.event;

import com.example.notification_system.entity.Notification;
import com.example.notification_system.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSubscriber {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleNotification(NotificationEvent event) {

        Notification notification = event.getNotification();

        // Step 1 - Save to PostgreSQL
        notificationRepository.save(notification);
        log.info("Notification saved to DB with id: {}", notification.getId());

        // Step 2 - Push via WebSocket
        if (notification.getGroupId() != null) {
            // Group broadcast -> send to /topic/{groupId}
            log.info("Broadcasting to group: {}", notification.getGroupId());
            messagingTemplate.convertAndSend(
                    "/topic/" + notification.getGroupId(),
                    notification
            );
        }
        else {
            // One-to-one → send to specific user
            log.info("Sending to user: {}", notification.getRecipientId());
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(notification.getRecipientId()),
                    "/queue/notifications",
                    notification
            );
        }
    }
}
