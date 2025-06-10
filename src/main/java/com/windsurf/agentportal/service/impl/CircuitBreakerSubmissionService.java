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
 * Circuit Breaker implementation for notification service calls to Agent Portal.
 * 
 * The Circuit Breaker pattern is a fault tolerance pattern that prevents cascading failures
 * by temporarily disabling calls to external systems when they're experiencing issues.
 * 
 * This implementation wraps the Agent Portal notification client calls in a circuit breaker
 * to handle failures gracefully. When the external service is experiencing issues, the circuit
 * opens after a configured number of failures, preventing further calls until a cooldown period.
 * 
 * Benefits:
 * - Prevents cascading failures across microservices
 * - Improves system resilience during partial outages
 * - Enables graceful degradation when dependent services are unavailable
 * - Allows for custom fallback behavior when the circuit is open
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
     * Send notification to Agent Portal with circuit breaker pattern implementation.
     * 
     * This method demonstrates the full circuit breaker pattern integration with OpenFeign clients:
     * 1. Creates a notification request with the required parameters
     * 2. Wraps the external service call in a circuit breaker
     * 3. Provides a fallback function for graceful handling of service failures
     * 
     * If the notification service is down or experiencing issues, the circuit will open
     * after a configured threshold of failures (defined in application.yml), and the
     * fallback function will be called instead of making the actual service call.
     * 
     * Circuit States:
     * - CLOSED: Normal operation, calls pass through to the service
     * - OPEN: After threshold failures, calls are immediately routed to fallback
     * - HALF-OPEN: After cooldown period, some calls are allowed to test if service recovered
     * 
     * @param userId User ID to notify in the Agent Portal system
     * @param submissionId Submission ID for the notification reference
     * @param status Status of the submission (e.g., "PROCESSED", "FAILED")
     * @param message Detailed message to send to the user
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
            // Fallback function that gets called when the circuit is open or the call fails
            throwable -> {
                log.error("Circuit breaker triggered when sending notification: {}", throwable.getMessage());
                
                // FALLBACK IMPLEMENTATION STRATEGIES:
                // --------------------------------
                // 1. Store notification in a local database for later retry
                //    Example: notificationRepository.save(createLocalNotification(userId, submissionId, message));
                //
                // 2. Use an alternative notification method
                //    Example: emailService.sendNotificationEmail(userId, submissionId, message);
                //
                // 3. Queue the notification for later processing using message broker
                //    Example: rabbitTemplate.convertAndSend("notification.queue", createNotificationMessage(...));
                //
                // 4. Log the failure for manual intervention
                //    Already implemented with the log.error above
                //
                // Note: In a production environment, you should implement at least one of these
                // fallback strategies to ensure notifications are eventually delivered
                
                return false; // Indicates fallback was triggered and primary action failed
            }
        );
    }
}
