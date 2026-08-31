package com.example.notification_system.repository;

import com.example.notification_system.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Fetch all unread notifications for a specific user (newest first)
    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);

    // Fetch all notifications for a specific user (read + unread)
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long RecipientId);

    // Fetch all notifications for a group
    List<Notification> findByGroupIdOrderByCreatedAtDesc(String groupId);

    // Count unread notifications for a user
    long countByRecipientIdAndIsReadFalse(Long recipientId);

    // Mark all notifications as read for a user
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipientId = :recipientId AND n.isRead = false")
    void markAllAsReadForUser(@Param("recipientId") Long recipientId);
}
