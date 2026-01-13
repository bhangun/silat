package tech.kayys.silat.registry.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import tech.kayys.silat.model.CommunicationType;
import tech.kayys.silat.model.ExecutorInfo;

/**
 * In-memory implementation of ExecutorRepository
 * In production, this would be replaced with a database implementation
 */
@ApplicationScoped
public class InMemoryExecutorRepository implements ExecutorRepository {
    
    private final Map<String, ExecutorInfo> executors = new ConcurrentHashMap<>();
    
    @Override
    public Uni<Void> save(ExecutorInfo executor) {
        executors.put(executor.executorId(), executor);
        return Uni.createFrom().voidItem();
    }
    
    @Override
    public Uni<Optional<ExecutorInfo>> findById(String executorId) {
        return Uni.createFrom().item(Optional.ofNullable(executors.get(executorId)));
    }
    
    @Override
    public Uni<List<ExecutorInfo>> findAll() {
        return Uni.createFrom().item(List.copyOf(executors.values()));
    }
    
    @Override
    public Uni<Void> delete(String executorId) {
        executors.remove(executorId);
        return Uni.createFrom().voidItem();
    }
    
    @Override
    public Uni<List<ExecutorInfo>> findByType(String executorType) {
        List<ExecutorInfo> filtered = executors.values().stream()
            .filter(executor -> executor.executorType().equals(executorType))
            .collect(Collectors.toList());
        return Uni.createFrom().item(filtered);
    }
    
    @Override
    public Uni<List<ExecutorInfo>> findByCommunicationType(CommunicationType communicationType) {
        List<ExecutorInfo> filtered = executors.values().stream()
            .filter(executor -> executor.communicationType() == communicationType)
            .collect(Collectors.toList());
        return Uni.createFrom().item(filtered);
    }
}