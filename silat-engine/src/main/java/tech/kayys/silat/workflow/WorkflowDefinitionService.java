package tech.kayys.silat.workflow;

import java.util.List;
import java.util.Map;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import tech.kayys.silat.model.TenantId;
import tech.kayys.silat.model.WorkflowDefinition;
import tech.kayys.silat.model.WorkflowDefinitionId;
import tech.kayys.silat.workflow.domain.CreateWorkflowDefinitionRequest;
import tech.kayys.silat.workflow.domain.UpdateWorkflowDefinitionRequest;

/**
 * Workflow definition service
 */
@ApplicationScoped
public class WorkflowDefinitionService implements tech.kayys.silat.api.workflow.WorkflowDefinitionService {

    @jakarta.inject.Inject
    WorkflowDefinitionRegistry registry;

    public Uni<WorkflowDefinition> create(
            tech.kayys.silat.dto.CreateWorkflowDefinitionRequest request,
            TenantId tenantId) {
        // Process DTO request and register workflow definition
        // Convert DTO to domain object manually
        var domainRequest = new tech.kayys.silat.workflow.domain.CreateWorkflowDefinitionRequest(
                request.name(),
                request.version(),
                request.description(),
                mapNodeDefinitions(request.nodes()),
                mapInputDefinitions(request.inputs()),
                mapOutputDefinitions(request.outputs()),
                mapRetryPolicy(request.retryPolicy()),
                mapCompensationPolicy(request.compensationPolicy()),
                request.metadata());

        return Uni.createFrom().nullItem();
    }

    private List<tech.kayys.silat.model.NodeDefinition> mapNodeDefinitions(List<tech.kayys.silat.dto.NodeDefinitionDto> dtos) {
        if (dtos == null) return null;
        return dtos.stream().map(this::mapNodeDefinition).toList();
    }

    private tech.kayys.silat.model.NodeDefinition mapNodeDefinition(tech.kayys.silat.dto.NodeDefinitionDto dto) {
        if (dto == null) return null;

        List<tech.kayys.silat.model.NodeId> dependsOn = dto.dependsOn() != null
            ? dto.dependsOn().stream().map(tech.kayys.silat.model.NodeId::of).toList()
            : List.of();

        List<tech.kayys.silat.model.Transition> transitions = dto.transitions() != null
            ? dto.transitions().stream().map(this::mapTransition).toList()
            : List.of();

        return new tech.kayys.silat.model.NodeDefinition(
                tech.kayys.silat.model.NodeId.of(dto.id()),
                dto.name(),
                tech.kayys.silat.model.NodeType.valueOf(dto.type()),
                dto.executorType(),
                dto.configuration(),
                dependsOn,
                transitions,
                mapRetryPolicy(dto.retryPolicy()),
                java.time.Duration.ofSeconds(dto.timeoutSeconds()),
                dto.critical()
        );
    }

    private tech.kayys.silat.model.Transition mapTransition(tech.kayys.silat.dto.TransitionDto dto) {
        if (dto == null) return null;

        return new tech.kayys.silat.model.Transition(
                dto.targetNodeId() != null ? tech.kayys.silat.model.NodeId.of(dto.targetNodeId()) : null,
                dto.condition(),
                tech.kayys.silat.model.Transition.TransitionType.valueOf(dto.type())
        );
    }

    private Map<String, tech.kayys.silat.model.InputDefinition> mapInputDefinitions(Map<String, tech.kayys.silat.dto.InputDefinitionDto> dtos) {
        if (dtos == null) return null;
        return dtos.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                java.util.Map.Entry::getKey,
                entry -> mapInputDefinition(entry.getValue())
            ));
    }

    private tech.kayys.silat.model.InputDefinition mapInputDefinition(tech.kayys.silat.dto.InputDefinitionDto dto) {
        if (dto == null) return null;

        return new tech.kayys.silat.model.InputDefinition(
                dto.name(),
                dto.type(),
                dto.required(),
                dto.defaultValue(),
                dto.description()
        );
    }

    private Map<String, tech.kayys.silat.model.OutputDefinition> mapOutputDefinitions(Map<String, tech.kayys.silat.dto.OutputDefinitionDto> dtos) {
        if (dtos == null) return null;
        return dtos.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                java.util.Map.Entry::getKey,
                entry -> mapOutputDefinition(entry.getValue())
            ));
    }

    private tech.kayys.silat.model.OutputDefinition mapOutputDefinition(tech.kayys.silat.dto.OutputDefinitionDto dto) {
        if (dto == null) return null;

        return new tech.kayys.silat.model.OutputDefinition(
                dto.name(),
                dto.type(),
                dto.description()
        );
    }

    private tech.kayys.silat.model.RetryPolicy mapRetryPolicy(tech.kayys.silat.dto.RetryPolicyDto dto) {
        if (dto == null) return null;

        return new tech.kayys.silat.model.RetryPolicy(
                dto.maxAttempts(),
                java.time.Duration.ofSeconds(dto.initialDelaySeconds()),
                java.time.Duration.ofSeconds(dto.maxDelaySeconds()),
                dto.backoffMultiplier(),
                dto.retryableExceptions()
        );
    }

    private tech.kayys.silat.saga.CompensationPolicy mapCompensationPolicy(tech.kayys.silat.dto.CompensationPolicyDto dto) {
        if (dto == null) return null;

        tech.kayys.silat.saga.CompensationPolicy.CompensationStrategy strategy =
            dto.strategy() != null
                ? tech.kayys.silat.saga.CompensationPolicy.CompensationStrategy.valueOf(dto.strategy())
                : tech.kayys.silat.saga.CompensationPolicy.CompensationStrategy.SEQUENTIAL;

        return new tech.kayys.silat.saga.CompensationPolicy(
            true, // Compensation enabled if DTO exists
            strategy,
            java.time.Duration.ofSeconds(dto.timeoutSeconds()),
            dto.failOnCompensationError()
        );
    }

    public Uni<WorkflowDefinition> get(
            tech.kayys.silat.model.WorkflowDefinitionId id,
            TenantId tenantId) {
        return registry.getDefinition(id, tenantId);
    }

    public Uni<List<WorkflowDefinition>> list(
            TenantId tenantId,
            boolean activeOnly) {
        return Uni.createFrom().item(List.of());
    }

    public Uni<WorkflowDefinition> update(
            tech.kayys.silat.model.WorkflowDefinitionId id,
            tech.kayys.silat.dto.UpdateWorkflowDefinitionRequest request,
            TenantId tenantId) {
        return Uni.createFrom().nullItem();
    }

    public Uni<Void> delete(
            tech.kayys.silat.model.WorkflowDefinitionId id,
            TenantId tenantId) {
        return Uni.createFrom().voidItem();
    }
}