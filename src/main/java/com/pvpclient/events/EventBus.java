package com.pvpclient.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A lightweight event bus for dispatching game events to subscribers.
 * Uses generic listeners keyed by event class to avoid casting overhead.
 */
public final class EventBus {

    private static final EventBus INSTANCE = new EventBus();

    /** Map from event class to list of typed listeners. */
    private final Map<Class<?>, List<Consumer<Object>>> listeners = new HashMap<>();

    private EventBus() {}

    public static EventBus getInstance() {
        return INSTANCE;
    }

    /**
     * Subscribe a listener to a specific event type.
     *
     * @param eventClass The class of the event to listen for.
     * @param listener   The callback to invoke when the event fires.
     * @param <T>        The event type.
     */
    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventClass, Consumer<T> listener) {
        listeners.computeIfAbsent(eventClass, k -> new ArrayList<>())
                 .add((Consumer<Object>) listener);
    }

    /**
     * Unsubscribe a listener from a specific event type.
     */
    @SuppressWarnings("unchecked")
    public <T> void unsubscribe(Class<T> eventClass, Consumer<T> listener) {
        List<Consumer<Object>> list = listeners.get(eventClass);
        if (list != null) {
            list.remove(listener);
        }
    }

    /**
     * Post an event to all registered listeners for its type.
     *
     * @param event The event instance to dispatch.
     */
    public void post(Object event) {
        List<Consumer<Object>> list = listeners.get(event.getClass());
        if (list == null || list.isEmpty()) return;
        // Iterate over a snapshot to allow unsubscription during dispatch.
        for (Consumer<Object> listener : new ArrayList<>(list)) {
            listener.accept(event);
        }
    }
}
