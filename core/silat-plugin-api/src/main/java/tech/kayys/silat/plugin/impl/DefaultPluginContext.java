package tech.kayys.silat.plugin.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import tech.kayys.silat.plugin.PluginContext;
import tech.kayys.silat.plugin.PluginMetadata;
import tech.kayys.silat.plugin.ServiceRegistry;
import tech.kayys.silat.plugin.EventBus;

/**
 * Default implementation of PluginContext
 */
public class DefaultPluginContext implements PluginContext {

    private final PluginMetadata metadata;
    private final Logger logger;
    private final Map<String, String> properties;
    private final ServiceRegistry serviceRegistry;
    private final EventBus eventBus;
    private final String dataDirectory;

    public DefaultPluginContext(
            PluginMetadata metadata,
            Logger logger,
            Map<String, String> properties,
            ServiceRegistry serviceRegistry,
            EventBus eventBus,
            String dataDirectory) {
        this.metadata = metadata;
        this.logger = logger;
        this.properties = new ConcurrentHashMap<>(properties);
        this.serviceRegistry = serviceRegistry;
        this.eventBus = eventBus;
        this.dataDirectory = dataDirectory;
    }

    @Override
    public PluginMetadata getMetadata() {
        return metadata;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public Optional<String> getProperty(String key) {
        return Optional.ofNullable(properties.get(key));
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    @Override
    public Map<String, String> getAllProperties() {
        return Map.copyOf(properties);
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public String getDataDirectory() {
        return dataDirectory;
    }
}
