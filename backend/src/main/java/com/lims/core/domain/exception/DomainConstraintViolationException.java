package com.lims.core.domain.exception;

import com.lims.governance.exception.GovernanceViolationException;

/**
 * Thrown when a Domain Object violates its internal invariants (e.g., missing identity).
 * Constitutional Principle: Failure Taxonomy (Mapped to I18nService).
 */
public class DomainConstraintViolationException extends GovernanceViolationException {
    
    public DomainConstraintViolationException(String fieldName) {
        super(
            "GOV-DOMAIN-001", // Domain Constraint Error Code
            String.format("Constraint violated: %s", fieldName)
        );
    }
    public DomainConstraintViolationException(String fieldName, String errorCode) {
        super(
            errorCode, 
            String.format("Domain Constraint Violated: %s", fieldName)
        );
    }
}