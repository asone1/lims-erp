package com.lims.governance.exception;

/**
 * Thrown when the BehaviorHash provided by the client does not match
 * the registered logic in the Governance Plane.
 */
public class ContractMismatchException extends GovernanceViolationException {

    public ContractMismatchException(String fieldId, String versionContract) {
        super(
            GovernanceErrorCode.CONTRACT_MISMATCH.getCode(),
            GovernanceErrorCode.CONTRACT_MISMATCH.formatMessage(fieldId, versionContract)
        );
    }

    public ContractMismatchException(String fieldId, String errorType, String details) {
        super(
            GovernanceErrorCode.CONTRACT_MISMATCH.getCode(),
            String.format("Governance Plane Block: [%s] %s. Details: %s", fieldId, errorType, details)
        );
    }
}