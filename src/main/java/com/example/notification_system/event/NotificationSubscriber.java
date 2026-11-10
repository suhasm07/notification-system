package com.example.notification_system.event;

import com.example.notification_system.entity.Notification;
import com.example.notification_system.repository.NotificationRepository;
import com.example.notification_system.service.BatchNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSubscriber {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final BatchNotificationService batchService;

    @Value("${notification.batching.enabled:true}")
    private boolean batchingEnabled;

    @EventListener
    public void handleNotification(NotificationEvent event) {
        Notification notification = event.getNotification();

        if (batchingEnabled) {
            // Optimized: deliver first, persist later in a batch
            push(notification);
            batchService.enqueue(notification);
        } else {
            // Baseline: one INSERT per notification, delivery waits for DB
            notificationRepository.save(notification);
            push(notification);
        }
    }

    private void push(Notification notification) {
        if (notification.getGroupId() != null) {
            messagingTemplate.convertAndSend("/topic/" + notification.getGroupId(), notification);
        } else {
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(notification.getRecipientId()),
                    "/queue/notifications",
                    notification
            );
        }
    }
}