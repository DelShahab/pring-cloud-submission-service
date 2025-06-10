package com.windsurf.agentportal.service.impl;

import com.windsurf.agentportal.client.AgentPortalNotifierClient;
import com.windsurf.agentportal.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Circuit Breaker implementation for notification service calls
 * This wraps the notification client calls in a circuit breaker to handle failures gracefully
 */
@Component
@Slf4j
public class CircuitBreakerSubmissionService {

    private final CircuitBreaker notificationCircuitBreaker;
    private final AgentPortalNotifierClient agentPortalNotifierClient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Value("${api.agent-portal.api-key}")
    private String agentPortalApiKey;

    public CircuitBreakerSubmissionService(
            CircuitBreakerFactory<?, ?> circuitBreakerFactory,
            AgentPortalNotifierClient agentPortalNotifierClient) {
        this.notificationCircuitBreaker = circuitBreakerFactory.create("notificationService");
        this.agentPortalNotifierClient = agentPortalNotifierClient;
    }

    /**
     * Send notification to Agent Portal with circuit breaker pattern
     * If the notification service is down, the circuit will open after a threshold of failures
     * 
     * @param userId User ID to notify
     * @param submissionId Submission ID for the notification
     * @param status Status of the submission
     * @param message Message to send
     */
    public void notifyUserWithCircuitBreaker(String userId, String submissionId, String status, String message) {
        log.debug("Sending notification to Agent Portal for userId: {} with circuit breaker", userId);
        
        NotificationRequest request = NotificationRequest.builder()
                .submissionId(submissionId)
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now().format(DATE_FORMATTER))
                .build();
        
        notificationCircuitBreaker.run(
            // The actual service call
            () -> {
                agentPortalNotifierClient.notifyUser(agentPortalApiKey, userId, request);
                log.info("Successfully sent notification to Agent Portal for userId: {}", userId);
                return true;
            },
            // Fallback function that gets called when the circuit is open
            throwable -> {
                log.error("Circuit breaker triggered when sending notification: {}", throwable.getMessage());
                // Here you would typically implement a fallback strategy:
                // 1. Store notification in a local database for later retry
                // 2. Use an alternative notification method
                // 3. Queue the notification for later processing
                return false;
            }
        );
    }
}
