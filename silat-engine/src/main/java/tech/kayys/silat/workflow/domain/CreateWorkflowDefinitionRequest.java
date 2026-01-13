package tech.kayys.silat.workflow.domain;

import java.util.List;
import java.util.Map;

import tech.kayys.silat.model.InputDefinition;
import tech.kayys.silat.model.NodeDefinition;
import tech.kayys.silat.model.OutputDefinition;
import tech.kayys.silat.model.RetryPolicy;
import tech.kayys.silat.saga.CompensationPolicy;

/**
 * Domain model for creating a workflow definition
 */
public record CreateWorkflowDefinitionRequest(
                String name,
                String version,
                String description,
                List<NodeDefinition> nodes,
                Map<String, InputDefinition> inputs,
                Map<String, OutputDefinition> outputs,
                RetryPolicy retryPolicy,
                CompensationPolicy compensationPolicy,
                Map<String, String> metadata) {
}