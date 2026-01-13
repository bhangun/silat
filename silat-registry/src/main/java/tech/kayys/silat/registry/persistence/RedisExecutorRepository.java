package tech.kayys.silat.registry.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import tech.kayys.silat.model.CommunicationType;
import tech.kayys.silat.model.ExecutorInfo;

/**
 * Redis-based implementation of ExecutorRepository
 */
@ApplicationScoped
public class RedisExecutorRepository implements ExecutorRepository {
    
    private static final Logger LOG = LoggerFactory.getLogger(RedisExecutorRepository.class);
    
    private static final String EXECUTOR_PREFIX = "executor:";
    private static final String EXECUTOR_TYPE_INDEX = "executor:types";
    private static final String EXECUTOR_COMM_TYPE_INDEX = "executor:comm_types";
    
    @Inject
    ReactiveRedisDataSource redis;
    
    private ReactiveValueCommands<String, String> valueCommands;
    
    @Override
    public Uni<Void> save(ExecutorInfo executor) {
        return Uni.createFrom().deferred(() -> {
            valueCommands = redis.value(String.class);
            
            // Store executor info as JSON
            String key = EXECUTOR_PREFIX + executor.executorId();
            String value = JsonObject.mapFrom(executor).encode();
            
            return valueCommands.set(key, value)
                .onItem().invoke(() -> {
                    // Update indexes
                    updateIndexes(executor);
                })
                .replaceWithVoid();
        });
    }
    
    @Override
    public Uni<Optional<ExecutorInfo>> findById(String executorId) {
        return Uni.createFrom().deferred(() -> {
            valueCommands = redis.value(String.class);
            
            String key = EXECUTOR_PREFIX + executorId;
            
            return valueCommands.get(key)
                .map(jsonStr -> {
                    if (jsonStr == null) {
                        return Optional.empty();
                    }
                    try {
                        ExecutorInfo executor = new JsonObject(jsonStr).mapFrom(ExecutorInfo.class);
                        return Optional.of(executor);
                    } catch (Exception e) {
                        LOG.error("Error deserializing executor: {}", executorId, e);
                        return Optional.empty();
                    }
                });
        });
    }
    
    @Override
    public Uni<List<ExecutorInfo>> findAll() {
        // This would require scanning all executor keys in Redis
        // For now, return empty list - in a real implementation, 
        // we'd maintain an index of all executor IDs
        return Uni.createFrom().item(java.util.Collections.emptyList());
    }
    
    @Override
    public Uni<Void> delete(String executorId) {
        return Uni.createFrom().deferred(() -> {
            valueCommands = redis.value(String.class);
            
            String key = EXECUTOR_PREFIX + executorId;
            
            return valueCommands.del(key)
                .replaceWithVoid();
        });
    }
    
    @Override
    public Uni<List<ExecutorInfo>> findByType(String executorType) {
        // In a real implementation, this would query the type index
        return Uni.createFrom().item(java.util.Collections.emptyList());
    }
    
    @Override
    public Uni<List<ExecutorInfo>> findByCommunicationType(CommunicationType communicationType) {
        // In a real implementation, this would query the communication type index
        return Uni.createFrom().item(java.util.Collections.emptyList());
    }
    
    private void updateIndexes(ExecutorInfo executor) {
        // Update indexes in Redis for faster queries
        // This would be implemented in a production system
    }
}