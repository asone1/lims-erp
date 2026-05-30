package com.lims.shared.domain;

/**
 * Constitutional Principle: Event Governance (Idempotency).
 * Ensures business events are processed exactly once.
 */
public interface Idempotent {
    String getCorrelationId();
}