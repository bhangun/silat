package tech.kayys.silat.registry;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import tech.kayys.silat.api.event.EventPublisher;
import tech.kayys.silat.model.event.ExecutionEvent;

/**
 * Default Event Publisher - Publishes domain events (stub implementation)
 */
@ApplicationScoped
public class EventPublisher implements EventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(EventPublisher.class);
    private static final String EVENT_TOPIC = "workflow.events";

    @Override
    public Uni<Void> publish(List<ExecutionEvent> events) {
        LOG.debug("Publishing {} events to topic: {}", events.size(), EVENT_TOPIC);

        // This is a stub implementation - the actual Kafka publisher is in KafkaEventPublisher
        return Uni.createFrom().voidItem();
    }

    @Override
    public Uni<Void> publishRetry(
            tech.kayys.silat.model.WorkflowRunId runId,
            tech.kayys.silat.model.NodeId nodeId) {
        ExecutionEvent event = new tech.kayys.silat.model.event.GenericExecutionEvent(
                runId,
                "RetryScheduled",
                "Node retry scheduled",
                java.time.Instant.now(),
                java.util.Map.of("nodeId", nodeId.value()));
        return publish(List.of(event));
    }
}