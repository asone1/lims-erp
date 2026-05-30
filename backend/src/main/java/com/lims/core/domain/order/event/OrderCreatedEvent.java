package com.lims.core.domain.order.event;

import com.lims.core.domain.order.Order;
import java.time.Instant;

/**
 * Business Event: Order has been successfully validated and persisted.
 */
public record OrderCreatedEvent(Order order, Instant occurredAt) {
    public OrderCreatedEvent(Order order) { this(order, Instant.now()); }
}