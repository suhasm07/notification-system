package com.example.notification_system.service;

import com.example.notification_system.dto.NotificationRequest;
import com.example.notification_system.entity.Notification;
import com.example.notification_system.event.NotificationPublisher;
import com.example.notification_system.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationPublisher publisher;
    private final NotificationRepository notificationRepository;

    // Send a notification (one-to-one or group)
    public void sendNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setSenderId(request.getSenderId());
        notification.setRecipientId(request.getRecipientId());
        notification.setGroupId(request.getGroupId());
        notification.setMessage(request.getMessage());

        log.info("Sending notification from user {} to recipient {} / group {}",
                request.getSenderId(),
                request.getRecipientId(),
                request.getGroupId());

        // Fire the event → Publisher → Subscriber handles the rest
        publisher.publish(notification);
    }

    // Get all unread notifications for a user
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    // Get all notifications for a user (read + unread)
    public List<Notification> getAllNotifications(Long userId) {
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    // Get all notifications for a group
    public List<Notification> getGroupNotifications(String groupId) {
        return notificationRepository
                .findByGroupIdOrderByCreatedAtDesc(groupId);
    }

    // Count unread notifications for a user
    public long countUnread(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    // Mark all as read for a user
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadForUser(userId);
        log.info("Marked all notifications as read for user: {}", userId);
    }
}
