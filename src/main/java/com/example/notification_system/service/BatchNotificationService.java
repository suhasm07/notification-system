package com.example.notification_system.service;

import com.example.notification_system.entity.Notification;
import com.example.notification_system.repository.NotificationRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchNotificationService {

    private static final int MAX_PER_FLUSH = 500;

    private final NotificationRepository notificationRepository;

    // Thread-safe: many WebSocket/HTTP threads add to it concurrently
    private final BlockingQueue<Notification> buffer = new LinkedBlockingQueue<>();

    public void enqueue(Notification notification) {
        buffer.add(notification);
    }

    // Runs 100ms after the previous flush finished
    @Scheduled(fixedDelay = 100)
    public void flush() {
        List<Notification> batch = new ArrayList<>();
        buffer.drainTo(batch, MAX_PER_FLUSH);

        if (batch.isEmpty()) {
            return;
        }

        try {
            notificationRepository.saveAll(batch);   // Hibernate groups into batches of 50
            log.debug("Flushed {} notifications to DB", batch.size());
        } catch (Exception e) {
            log.error("Failed to flush {} notifications", batch.size(), e);
        }
    }

    // Don't lose buffered messages on shutdown
    @PreDestroy
    public void flushOnShutdown() {
        while (!buffer.isEmpty()) {
            flush();
        }
    }
}