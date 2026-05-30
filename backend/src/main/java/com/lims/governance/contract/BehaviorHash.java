package com.lims.governance.contract;

/**
 * BehaviorHash represents a deterministic logic fingerprint.
 * Must be public to be accessible across governance packages.
 */
public record BehaviorHash(String value) {
    public BehaviorHash {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BehaviorHash cannot be empty.");
        }
    }
}
