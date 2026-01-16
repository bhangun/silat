package tech.kayys.silat.registry;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import tech.kayys.silat.model.ExecutorInfo;
import tech.kayys.silat.model.NodeId;

/**
 * Round-robin executor selection strategy
 */
public class RoundRobinSelectionStrategy implements ExecutorSelectionStrategy {
    
    private final Map<String, Integer> selectionIndex = new ConcurrentHashMap<>();
    
    @Override
    public Optional<ExecutorInfo> select(NodeId nodeId, List<ExecutorInfo> availableExecutors, Map<String, Object> context) {
        if (availableExecutors.isEmpty()) {
            return Optional.empty();
        }
        
        String nodeKey = nodeId.value();
        int currentIndex = selectionIndex.getOrDefault(nodeKey, 0);
        int nextIndex = (currentIndex + 1) % availableExecutors.size();
        selectionIndex.put(nodeKey, nextIndex);
        
        ExecutorInfo selected = availableExecutors.get(currentIndex % availableExecutors.size());
        return Optional.of(selected);
    }
    
    @Override
    public String getName() {
        return "round-robin";
    }
}