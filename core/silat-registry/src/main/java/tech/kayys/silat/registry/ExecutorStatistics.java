package tech.kayys.silat.registry;

import java.util.Map;

/**
 * Executor registry statistics
 */
public record ExecutorStatistics(
    int totalExecutors,
    int healthyExecutors,
    int unhealthyExecutors,
    Map<String, Integer> executorsByType,
    Map<tech.kayys.silat.model.CommunicationType, Integer> executorsByCommunicationType,
    long lastUpdatedTimestamp
) {
}