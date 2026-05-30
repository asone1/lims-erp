package com.lims.governance.contract;

import java.time.Instant;

/**
 * ContractSignature represents the immutable "Three-in-One" contract.
 * Any change in logic or structure requires a new BehaviorHash and Version.
 */
public record ContractSignature(
    String fieldId,
    String version,             // Semantic version (e.g., "1.2.0")
    BehaviorHash behaviorHash,  // The logic fingerprint
    SchemaDefinition inputSchema,
    SchemaDefinition outputSchema,
    ContractStatus status,
    Instant createdAt
) {
    public ContractSignature {
        if (fieldId == null || behaviorHash == null) {
            throw new IllegalArgumentException("Governance Violation: Contract must have FieldID and BehaviorHash.");
        }
    }
}



record SchemaDefinition(String jsonSchema) {}

enum ContractStatus {
    ACTIVE, DEPRECATED, FROZEN, REVOKED
}