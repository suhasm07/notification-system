package com.example.notification_system.event;

import com.example.notification_system.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publish(Notification notification){
        log.info("Publishing notification event for recipient: {}",
                notification.getRecipientId());
        NotificationEvent event = new NotificationEvent(this, notification);
        eventPublisher.publishEvent(event);
    }
}
