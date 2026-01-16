package tech.kayys.silat.plugin.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import jakarta.enterprise.context.ApplicationScoped;
import tech.kayys.silat.plugin.EventBus;
import tech.kayys.silat.plugin.PluginEvent;

/**
 * Default implementation of EventBus
 */
@ApplicationScoped
public class DefaultEventBus implements EventBus {

    private final Map<Class<? extends PluginEvent>, List<Consumer<? extends PluginEvent>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void publish(PluginEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        Class<? extends PluginEvent> eventType = event.getClass();
        List<Consumer<? extends PluginEvent>> handlers = subscribers.get(eventType);

        if (handlers != null) {
            for (Consumer<? extends PluginEvent> handler : handlers) {
                try {
                    @SuppressWarnings("unchecked")
                    Consumer<PluginEvent> typedHandler = (Consumer<PluginEvent>) handler;
                    typedHandler.accept(event);
                } catch (Exception e) {
                    // Log but don't fail on handler errors
                    System.err.println("Error in event handler: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public <T extends PluginEvent> Subscription subscribe(Class<T> eventType, Consumer<T> handler) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        List<Consumer<? extends PluginEvent>> handlers = subscribers.computeIfAbsent(
                eventType,
                k -> new CopyOnWriteArrayList<>());
        handlers.add(handler);

        return () -> handlers.remove(handler);
    }
}
