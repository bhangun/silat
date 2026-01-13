package tech.kayys.silat.registry.example;

import java.time.Duration;
import java.util.Map;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import tech.kayys.silat.model.CommunicationType;
import tech.kayys.silat.model.ExecutorInfo;
import tech.kayys.silat.model.NodeId;
import tech.kayys.silat.registry.ExecutorRegistry;

/**
 * Example usage of the Silat Registry
 */
@ApplicationScoped
public class RegistryUsageExample {

    @Inject
    ExecutorRegistry registry;

    /**
     * Example: Register an executor
     */
    public Uni<Void> registerExecutorExample() {
        ExecutorInfo executor = new ExecutorInfo(
            "executor-1",
            "java-worker",
            CommunicationType.GRPC,
            "localhost:8080",
            Duration.ofSeconds(30),
            Map.of("region", "us-east-1", "capacity", "high", "version", "1.0.0")
        );

        return registry.registerExecutor(executor)
            .invoke(() -> System.out.println("Executor registered: " + executor.executorId()));
    }

    /**
     * Example: Discover executor for a node
     */
    public Uni<Void> discoverExecutorExample() {
        NodeId nodeId = NodeId.of("workflow-node-1");

        return registry.getExecutorForNode(nodeId)
            .invoke(executorOpt -> {
                if (executorOpt.isPresent()) {
                    System.out.println("Selected executor: " + executorOpt.get().executorId());
                } else {
                    System.out.println("No healthy executor available for node: " + nodeId.value());
                }
            });
    }

    /**
     * Example: Send heartbeat from executor
     */
    public Uni<Void> heartbeatExample() {
        return registry.heartbeat("executor-1")
            .invoke(() -> System.out.println("Heartbeat sent for executor-1"));
    }

    /**
     * Example: Get registry statistics
     */
    public Uni<Void> getStatsExample() {
        return registry.getStatistics()
            .invoke(stats -> {
                System.out.println("Registry Statistics:");
                System.out.println("  Total Executors: " + stats.totalExecutors());
                System.out.println("  Healthy Executors: " + stats.healthyExecutors());
                System.out.println("  Unhealthy Executors: " + stats.unhealthyExecutors());
                System.out.println("  Executors by Type: " + stats.executorsByType());
                System.out.println("  Executors by Comm Type: " + stats.executorsByCommunicationType());
            });
    }

    /**
     * Example: Update executor metadata
     */
    public Uni<Void> updateMetadataExample() {
        Map<String, String> newMetadata = Map.of(
            "region", "us-west-2",
            "capacity", "medium",
            "updated", String.valueOf(System.currentTimeMillis())
        );

        return registry.updateExecutorMetadata("executor-1", newMetadata)
            .invoke(() -> System.out.println("Updated metadata for executor-1"));
    }

    /**
     * Example: Complete workflow demonstrating registry usage
     */
    public Uni<Void> completeWorkflowExample() {
        return registerExecutorExample()
            .chain(this::heartbeatExample)
            .chain(this::discoverExecutorExample)
            .chain(this::updateMetadataExample)
            .chain(this::getStatsExample)
            .invoke(() -> System.out.println("Registry usage example completed successfully"));
    }
}