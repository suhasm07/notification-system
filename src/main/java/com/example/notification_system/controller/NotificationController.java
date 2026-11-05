package com.example.notification_system.controller;

import com.example.notification_system.dto.NotificationRequest;
import com.example.notification_system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    // Client sends to /app/notify
    // @MessageMapping("/notify") handles it
    @MessageMapping("/notify")
    public void sendNotification(@Payload NotificationRequest request) {
        log.info("WebSocket message received from sender: {}", request.getSenderId());
        notificationService.sendNotification(request);
    }

    // Client sends to /app/notify/group
    // For explicit group notifications
    @MessageMapping("/notify/group")
    public void sendGroupNotification(@Payload NotificationRequest request) {
        log.info("Group WebSocket message received for group: {}", request.getGroupId());
        notificationService.sendNotification(request);
    }
}
