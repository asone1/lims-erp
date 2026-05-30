package com.lims.shared.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Constitutional Principle: Event Governance.
 * All Business Events must carry provenance metadata for auditing.
 */
public interface BusinessEvent {
    UUID getEventId();      // Unique ID for the event instance
    Instant getOccurredAt();
    String getVersion();    // Schema Version for Versioned Runtime Contract
}