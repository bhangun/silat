package tech.kayys.silat.runtime.mapper;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tech.kayys.silat.dto.CompensationPolicyDto;
import tech.kayys.silat.dto.InputDefinitionDto;
import tech.kayys.silat.dto.NodeDefinitionDto;
import tech.kayys.silat.dto.OutputDefinitionDto;
import tech.kayys.silat.dto.RetryPolicyDto;
import tech.kayys.silat.dto.TransitionDto;
import tech.kayys.silat.model.InputDefinition;
import tech.kayys.silat.model.NodeDefinition;
import tech.kayys.silat.model.OutputDefinition;
import tech.kayys.silat.model.RetryPolicy;
import tech.kayys.silat.model.Transition;
import tech.kayys.silat.model.NodeId;
import tech.kayys.silat.model.NodeType;

/**
 * Mapper utility to convert between DTOs and domain models
 */
public class WorkflowDefinitionMapper {

    /**
     * Maps DTO CreateWorkflowDefinitionRequest to domain
     * CreateWorkflowDefinitionRequest
     */
    public static tech.kayys.silat.workflow.domain.CreateWorkflowDefinitionRequest toDomain(
            tech.kayys.silat.dto.CreateWorkflowDefinitionRequest dto) {
        if (dto == null) {
            return null;
        }

        List<tech.kayys.silat.model.NodeDefinition> nodeDefinitions = mapNodeDefinitions(dto.nodes());
        Map<String, tech.kayys.silat.model.InputDefinition> inputDefinitions = mapInputDefinitions(dto.inputs());
        Map<String, tech.kayys.silat.model.OutputDefinition> outputDefinitions = mapOutputDefinitions(dto.outputs());

        return new tech.kayys.silat.workflow.domain.CreateWorkflowDefinitionRequest(
                dto.name(),
                dto.version(),
                dto.description(),
                nodeDefinitions,
                inputDefinitions,
                outputDefinitions,
                mapRetryPolicy(dto.retryPolicy()),
                mapCompensationPolicy(dto.compensationPolicy()), // This should return the correct type
                dto.metadata());
    }

    /**
     * Maps DTO UpdateWorkflowDefinitionRequest to domain
     * UpdateWorkflowDefinitionRequest
     */
    public static tech.kayys.silat.workflow.domain.UpdateWorkflowDefinitionRequest toDomain(
            tech.kayys.silat.dto.UpdateWorkflowDefinitionRequest dto) {
        if (dto == null) {
            return null;
        }

        return new tech.kayys.silat.workflow.domain.UpdateWorkflowDefinitionRequest(
                dto.description(),
                dto.isActive(),
                dto.metadata());
    }

    private static List<tech.kayys.silat.model.NodeDefinition> mapNodeDefinitions(List<NodeDefinitionDto> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(WorkflowDefinitionMapper::mapNodeDefinition)
                .collect(Collectors.toList());
    }

    private static tech.kayys.silat.model.NodeDefinition mapNodeDefinition(NodeDefinitionDto dto) {
        if (dto == null) {
            return null;
        }

        // Convert dependsOn from String to NodeId
        List<NodeId> dependsOn = dto.dependsOn() != null
                ? dto.dependsOn().stream()
                        .map(NodeId::of)
                        .collect(Collectors.toList())
                : List.of();

        // Convert transitions
        List<tech.kayys.silat.model.Transition> transitions = dto.transitions() != null
                ? dto.transitions().stream()
                        .map(WorkflowDefinitionMapper::mapTransition)
                        .collect(Collectors.toList())
                : List.of();

        // Convert timeout
        Duration timeout = dto.timeoutSeconds() != null
                ? Duration.ofSeconds(dto.timeoutSeconds())
                : Duration.ZERO;

        return new tech.kayys.silat.model.NodeDefinition(
                NodeId.of(dto.id()),
                dto.name(),
                NodeType.valueOf(dto.type()),
                dto.executorType(),
                dto.configuration(),
                dependsOn,
                transitions,
                mapRetryPolicy(dto.retryPolicy()),
                timeout,
                dto.critical());
    }

    private static tech.kayys.silat.model.Transition mapTransition(TransitionDto dto) {
        if (dto == null) {
            return null;
        }

        return new tech.kayys.silat.model.Transition(
                dto.targetNodeId() != null ? NodeId.of(dto.targetNodeId()) : null,
                dto.condition(),
                tech.kayys.silat.model.Transition.TransitionType.valueOf(dto.type()));
    }

    private static Map<String, tech.kayys.silat.model.InputDefinition> mapInputDefinitions(Map<String, InputDefinitionDto> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> mapInputDefinition(entry.getValue())));
    }

    private static tech.kayys.silat.model.InputDefinition mapInputDefinition(InputDefinitionDto dto) {
        if (dto == null) {
            return null;
        }

        return new tech.kayys.silat.model.InputDefinition(
                dto.name(),
                dto.type(),
                dto.required(),
                dto.defaultValue(),
                dto.description());
    }

    private static Map<String, tech.kayys.silat.model.OutputDefinition> mapOutputDefinitions(Map<String, OutputDefinitionDto> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> mapOutputDefinition(entry.getValue())));
    }

    private static tech.kayys.silat.model.OutputDefinition mapOutputDefinition(OutputDefinitionDto dto) {
        if (dto == null) {
            return null;
        }

        return new tech.kayys.silat.model.OutputDefinition(
                dto.name(),
                dto.type(),
                dto.description());
    }

    private static tech.kayys.silat.model.RetryPolicy mapRetryPolicy(RetryPolicyDto dto) {
        if (dto == null) {
            return null;
        }

        return new tech.kayys.silat.model.RetryPolicy(
                dto.maxAttempts(),
                Duration.ofSeconds(dto.initialDelaySeconds()),
                Duration.ofSeconds(dto.maxDelaySeconds()),
                dto.backoffMultiplier(),
                dto.retryableExceptions() // This should be the list of exceptions
        );
    }

    private static tech.kayys.silat.saga.CompensationPolicy mapCompensationPolicy(CompensationPolicyDto dto) {
        if (dto == null) {
            return null;
        }

        // Convert strategy string to enum
        tech.kayys.silat.saga.CompensationPolicy.CompensationStrategy strategy = dto.strategy() != null
                ? tech.kayys.silat.saga.CompensationPolicy.CompensationStrategy.valueOf(dto.strategy())
                : tech.kayys.silat.saga.CompensationPolicy.CompensationStrategy.SEQUENTIAL;

        // If DTO exists, compensation is enabled
        return new tech.kayys.silat.saga.CompensationPolicy(
                true, // Compensation is enabled if DTO is present
                strategy,
                java.time.Duration.ofSeconds(dto.timeoutSeconds()),
                dto.failOnCompensationError());
    }
}