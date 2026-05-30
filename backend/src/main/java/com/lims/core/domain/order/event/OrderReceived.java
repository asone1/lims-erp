package com.lims.core.domain.order.event;

import com.lims.governance.contract.ContractRegistry;
import com.lims.shared.domain.BusinessEvent;
import java.time.Instant;
import java.util.UUID;

public record OrderReceived(UUID eventId, UUID orderId, String correlationId, Instant occurredAt) implements BusinessEvent {
    @Override public String getVersion() { return ContractRegistry.LimsResultEvents.RECEIVED_V1; }
    @Override public UUID getEventId() { return eventId; }
    @Override public Instant getOccurredAt() { return occurredAt; }
}