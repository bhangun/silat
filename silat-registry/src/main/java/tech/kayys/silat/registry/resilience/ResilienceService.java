package tech.kayys.silat.registry.resilience;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;

/**
 * Resilience service for the executor registry
 */
public class ResilienceService {
    
    private static final Logger LOG = LoggerFactory.getLogger(ResilienceService.class);
    
    // Circuit breaker state tracking
    private final Map<String, CircuitBreakerState> circuitBreakers = new ConcurrentHashMap<String, CircuitBreakerState>();
    
    // Retry configuration
    private final Duration baseDelay = Duration.ofSeconds(1);
    private final Duration maxDelay = Duration.ofSeconds(30);
    private final int maxRetries = 3;
    
    /**
     * Execute an operation with circuit breaker protection
     */
    public <T> Uni<T> executeWithCircuitBreaker(String operationName, Uni<T> operation) {
        CircuitBreakerState state = circuitBreakers.computeIfAbsent(operationName, k -> new CircuitBreakerState());
        
        if (state.isOpen()) {
            if (state.canAttemptReset()) {
                LOG.debug("Circuit breaker for {} is half-open, allowing one attempt", operationName);
                return executeAndHandleCircuitBreaker(operationName, operation, state, true);
            } else {
                LOG.warn("Circuit breaker for {} is OPEN, failing fast", operationName);
                Throwable breakerFailure = new RuntimeException("Circuit breaker is open for " + operationName);
                return Uni.createFrom().failure(breakerFailure);
            }
        }
        
        return executeAndHandleCircuitBreaker(operationName, operation, state, false);
    }
    
    private <T> Uni<T> executeAndHandleCircuitBreaker(String operationName, Uni<T> operation, CircuitBreakerState state, boolean halfOpen) {
        return operation
            .onItem().transformToUni(item -> {
                if (halfOpen) {
                    // Success in half-open state means we can close the circuit
                    state.close();
                    LOG.info("Circuit breaker for {} closed after successful operation", operationName);
                }
                state.recordSuccess();
                return Uni.createFrom().item(item);
            })
            .onFailure().recoverWithUni((Throwable failure) -> {
                state.recordFailure();
                
                if (state.failureCount >= state.failureThreshold) {
                    state.open();
                    LOG.warn("Circuit breaker for {} opened after {} consecutive failures", 
                             operationName, state.failureCount);
                }
                
                return Uni.createFrom().failure(failure);
            });
    }
    
    /**
     * Execute an operation with retry logic
     */
    public <T> Uni<T> executeWithRetry(String operationName, java.util.function.Supplier<Uni<T>> operation) {
        return executeWithRetry(operationName, operation, 0);
    }
    
    private <T> Uni<T> executeWithRetry(String operationName, java.util.function.Supplier<Uni<T>> operation, int attempt) {
        return operation.get()
            .onFailure().retry()
            .withBackOff(
                Duration.ofSeconds((long) Math.min(Math.pow(2, attempt), maxDelay.getSeconds())),
                maxDelay)
            .atMost(maxRetries);
    }
    
    /**
     * Execute an operation with timeout
     */
    public <T> Uni<T> executeWithTimeout(String operationName, Uni<T> operation, Duration timeout) {
        return operation
            .ifNoItem().after(timeout)
            .failWith(() -> {
                LOG.warn("Operation {} timed out after {}", operationName, timeout);
                return new RuntimeException("Operation " + operationName + " timed out");
            });
    }
    
    /**
     * Circuit breaker state
     */
    private static class CircuitBreakerState {
        private volatile State currentState = State.CLOSED;
        private long lastFailureTime = 0;
        private int failureCount = 0;
        private final int failureThreshold = 5;
        private final Duration timeout = Duration.ofSeconds(30);
        
        enum State {
            CLOSED, OPEN, HALF_OPEN
        }
        
        boolean isOpen() {
            return currentState == State.OPEN;
        }
        
        boolean canAttemptReset() {
            return currentState == State.OPEN && 
                   System.currentTimeMillis() - lastFailureTime > timeout.toMillis();
        }
        
        void open() {
            currentState = State.OPEN;
            lastFailureTime = System.currentTimeMillis();
        }
        
        void close() {
            currentState = State.CLOSED;
            failureCount = 0;
        }
        
        void recordSuccess() {
            if (currentState == State.HALF_OPEN) {
                close();
            }
        }
        
        void recordFailure() {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();
            
            if (failureCount >= failureThreshold) {
                open();
            }
        }
    }
}