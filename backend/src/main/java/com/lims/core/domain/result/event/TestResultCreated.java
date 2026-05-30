package com.lims.core.domain.result.event;

import com.lims.governance.contract.ContractRegistry;
import com.lims.shared.domain.BusinessEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable Domain Event: TestResultCreated.
 * Constitutional Principle: Event Governance (Fact-based).
 */
public record TestResultCreated(
    UUID eventId,
    UUID resultId,
    UUID sampleId,
    String analystId,
    String correlationId,
    Instant occurredAt
) implements BusinessEvent {

    public TestResultCreated {
        if (resultId == null || correlationId == null) {
            throw new IllegalArgumentException("Governance Violation: Event must have resultId and correlationId.");
        }
    }

    // --- Contract Implementation (Bridging Record Accessors to Interface) ---

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

  @Override
    public String getVersion() {
        // Reference the Registry instead of hardcoding
        return ContractRegistry.LimsResultEvents.CREATED_V1;
    }
}