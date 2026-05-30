package com.lims.governance.exception;

/**
 * Base exception for all Governance Plane violations.
 * Supports Failure Taxonomy by enforcing a standardized error code.
 */
public abstract class GovernanceViolationException extends RuntimeException {
    private final String errorCode;

    public GovernanceViolationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Returns a user-friendly semantic message for the UI.
     */
    public String getBusinessMessage() {
        return getMessage();
    }
}