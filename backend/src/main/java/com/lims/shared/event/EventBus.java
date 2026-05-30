package com.lims.shared.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventBus {
    private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);

    @Inject Event<Object> eventDispatcher;

    public void publish(Object event) {
        LOG.debug("Publishing Business Event: {}", event.getClass().getSimpleName());
        eventDispatcher.fire(event);
    }
}