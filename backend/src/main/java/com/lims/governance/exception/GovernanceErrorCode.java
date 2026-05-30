package com.lims.governance.exception;

/**
 * Centralized registry for Governance Plane error codes.
 * Constitutional Principle: Failure Taxonomy & Predictability.
 * Strictly English messages with traceability placeholders.
 */
public enum GovernanceErrorCode {
    CAPABILITY_FROZEN_DOMAIN("GOV-CAP-001", "Domain identity [%s] is in FROZEN state. Modification prohibited."),
    SCHEMA_MISSING_PROPERTIES("GOV-SCHEMA-001", "Schema Violation: Entity [%s] is missing mandatory extended properties."),
    CONTRACT_MISMATCH("GOV-CONTRACT-001", "Contract Violation: Field [%s] logic hash mismatch for version mapping [%s]."),
    MISSING_GOVERNANCE_HEADERS("GOV-SYS-001", "Security Block: Missing mandatory governance headers in request.");

    private final String code;
    private final String messageTemplate;

    GovernanceErrorCode(String code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    public String getCode() { return code; }
    public String formatMessage(Object... args) { return String.format(messageTemplate, args); }
}