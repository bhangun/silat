package tech.kayys.silat.api.workflow;

import java.util.List;

import io.smallrye.mutiny.Uni;
import tech.kayys.silat.dto.CreateWorkflowDefinitionRequest;
import tech.kayys.silat.dto.UpdateWorkflowDefinitionRequest;
import tech.kayys.silat.model.TenantId;
import tech.kayys.silat.model.WorkflowDefinition;
import tech.kayys.silat.model.WorkflowDefinitionId;

/**
 * Workflow definition service interface
 */
public interface WorkflowDefinitionService {

    Uni<WorkflowDefinition> create(
            CreateWorkflowDefinitionRequest request,
            TenantId tenantId);

    Uni<WorkflowDefinition> get(
            WorkflowDefinitionId id,
            TenantId tenantId);

    Uni<List<WorkflowDefinition>> list(
            TenantId tenantId,
            boolean activeOnly);

    Uni<WorkflowDefinition> update(
            WorkflowDefinitionId id,
            UpdateWorkflowDefinitionRequest request,
            TenantId tenantId);

    Uni<Void> delete(
            WorkflowDefinitionId id,
            TenantId tenantId);
}