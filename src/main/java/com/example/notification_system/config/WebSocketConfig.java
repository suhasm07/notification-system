package com.example.notification_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Clients subscribe to destinations starting with these prefixes
        // /topic  → group broadcasts (one to many)
        // /user   → private messages (one to one)
        config.enableSimpleBroker("/topic", "/user");

        // Prefix for messages FROM client TO server
        // Client sends to /app/notify → server handles it
        config.setApplicationDestinationPrefixes("/app");

        // Prefix for user-specific destinations
        // Enables /user/{userId}/queue/notifications routing
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // This is the URL clients connect to first (handshake)
        // ws://localhost:8080/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // allow all origins (for dev)
                .withSockJS();                   // fallback for browsers that don't support WS
    }

}
