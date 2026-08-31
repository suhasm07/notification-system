package com.example.notification_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private Long senderId;
    private Long recipientId;   // null for group messages
    private String groupId;     // null for one-to-one messages
    private String message;
}