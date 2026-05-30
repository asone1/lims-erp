package com.lims.shared.domain;

/**
 * Constitutional Principle: Contract Aggregation.
 * Every Aggregate Root in the system must conform to this standard set of traits.
 * This ensures consistency and simplifies the domain model implementation.
 */
public interface ConstitutionalAggregate extends Auditable, Idempotent, Exportable, Reportable {
    // This is a marker interface for all valid Domain Aggregates.
}