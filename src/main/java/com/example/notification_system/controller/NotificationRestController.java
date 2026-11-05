package com.example.notification_system.controller;

import com.example.notification_system.dto.NotificationRequest;
import com.example.notification_system.entity.Notification;
import com.example.notification_system.entity.User;
import com.example.notification_system.repository.UserRepository;
import com.example.notification_system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class NotificationRestController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    // ================================
    // NOTIFICATION ENDPOINTS
    // ================================

    // Send notification via REST (alternative to WebSocket)
    // POST http://localhost:8080/api/notifications/send
    @PostMapping("/notifications/send")
    public ResponseEntity<String> sendNotification(
            @RequestBody NotificationRequest request) {
        log.info("REST request to send notification from: {}", request.getSenderId());
        notificationService.sendNotification(request);
        return ResponseEntity.ok("Notification sent successfully");
    }

    // Get all unread notifications for a user
    // GET http://localhost:8080/api/notifications/unread/1
    @GetMapping("/notifications/unread/{userId}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @PathVariable Long userId) {
        log.info("Fetching unread notifications for user: {}", userId);
        List<Notification> notifications =
                notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // Get all notifications for a user (read + unread)
    // GET http://localhost:8080/api/notifications/all/1
    @GetMapping("/notifications/all/{userId}")
    public ResponseEntity<List<Notification>> getAllNotifications(
            @PathVariable Long userId) {
        log.info("Fetching all notifications for user: {}", userId);
        List<Notification> notifications =
                notificationService.getAllNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // Get all notifications for a group
    // GET http://localhost:8080/api/notifications/group/team-42
    @GetMapping("/notifications/group/{groupId}")
    public ResponseEntity<List<Notification>> getGroupNotifications(
            @PathVariable String groupId) {
        log.info("Fetching notifications for group: {}", groupId);
        List<Notification> notifications =
                notificationService.getGroupNotifications(groupId);
        return ResponseEntity.ok(notifications);
    }

    // Count unread notifications for a user
    // GET http://localhost:8080/api/notifications/count/1
    @GetMapping("/notifications/count/{userId}")
    public ResponseEntity<Long> countUnread(@PathVariable Long userId) {
        long count = notificationService.countUnread(userId);
        return ResponseEntity.ok(count);
    }

    // Mark all notifications as read for a user
    // PUT http://localhost:8080/api/notifications/read/1
    @PutMapping("/notifications/read/{userId}")
    public ResponseEntity<String> markAllAsRead(@PathVariable Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("All notifications marked as read");
    }

    // ================================
    // USER ENDPOINTS
    // ================================

    // Create a new user
    // POST http://localhost:8080/api/users
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Creating new user: {}", user.getUsername());
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // Get all users
    // GET http://localhost:8080/api/users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Get user by ID
    // GET http://localhost:8080/api/users/1
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}