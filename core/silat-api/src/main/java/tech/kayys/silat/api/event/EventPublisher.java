package tech.kayys.silat.api.event;

import java.util.List;

import io.smallrye.mutiny.Uni;
import tech.kayys.silat.model.event.ExecutionEvent;

/**
 * Event Publisher API - Publishes domain events
 */
public interface EventPublisher {

    Uni<Void> publish(List<ExecutionEvent> events);

    Uni<Void> publishRetry(
            tech.kayys.silat.model.WorkflowRunId runId,
            tech.kayys.silat.model.NodeId nodeId);
}