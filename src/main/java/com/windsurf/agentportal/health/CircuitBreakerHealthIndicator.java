package com.windsurf.agentportal.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;

/**
 * Custom health indicator that reports the status of the circuit breaker.
 * This provides visibility into the circuit breaker state through the /actuator/health endpoint.
 */
@Component
@RequiredArgsConstructor
public class CircuitBreakerHealthIndicator implements HealthIndicator {

    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    
    private static final String CIRCUIT_BREAKER_NAME = "notificationService";
    private static final String DETAIL_CIRCUIT_BREAKER = "circuitBreaker";
    private static final String DETAIL_STATE = "state";
    private static final String DETAIL_METRICS = "metrics";
    private static final String DETAIL_ERROR = "error";
    private static final String STATUS_DEGRADED = "DEGRADED";
    
    @Override
    public Health health() {
        try {
            // Get the circuit breaker instance
            io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker = 
                ((io.github.resilience4j.circuitbreaker.CircuitBreaker) circuitBreakerFactory.create(CIRCUIT_BREAKER_NAME));
            
            // Get the state of the circuit breaker
            CircuitBreaker.State state = circuitBreaker.getState();
            
            // Health status depends on circuit breaker state
            if (state == CircuitBreaker.State.CLOSED) {
                return Health.up()
                        .withDetail(DETAIL_CIRCUIT_BREAKER, CIRCUIT_BREAKER_NAME)
                        .withDetail(DETAIL_STATE, state)
                        .withDetail(DETAIL_METRICS, buildMetricsMap(circuitBreaker))
                        .build();
            } else if (state == CircuitBreaker.State.HALF_OPEN) {
                return Health.status(STATUS_DEGRADED)
                        .withDetail(DETAIL_CIRCUIT_BREAKER, CIRCUIT_BREAKER_NAME)
                        .withDetail(DETAIL_STATE, state)
                        .withDetail(DETAIL_METRICS, buildMetricsMap(circuitBreaker))
                        .build();
            } else {
                return Health.down()
                        .withDetail(DETAIL_CIRCUIT_BREAKER, CIRCUIT_BREAKER_NAME)
                        .withDetail(DETAIL_STATE, state)
                        .withDetail(DETAIL_METRICS, buildMetricsMap(circuitBreaker))
                        .build();
            }
        } catch (Exception e) {
            return Health.unknown()
                    .withDetail(DETAIL_CIRCUIT_BREAKER, CIRCUIT_BREAKER_NAME)
                    .withDetail(DETAIL_ERROR, "Unable to determine circuit breaker state: " + e.getMessage())
                    .build();
        }
    }
    
    private java.util.Map<String, Object> buildMetricsMap(CircuitBreaker circuitBreaker) {
        java.util.Map<String, Object> metrics = new java.util.HashMap<>();
        
        // Add relevant metrics from the circuit breaker
        metrics.put("failureRate", circuitBreaker.getMetrics().getFailureRate());
        metrics.put("slowCallRate", circuitBreaker.getMetrics().getSlowCallRate());
        metrics.put("numberOfSuccessfulCalls", circuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
        metrics.put("numberOfFailedCalls", circuitBreaker.getMetrics().getNumberOfFailedCalls());
        metrics.put("numberOfSlowCalls", circuitBreaker.getMetrics().getNumberOfSlowCalls());
        
        return metrics;
    }
}
