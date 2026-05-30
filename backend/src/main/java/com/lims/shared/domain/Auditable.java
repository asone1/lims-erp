package com.lims.shared.domain;

/**
 * Constitutional Principle: Intent-based Audit Trail.
 * All Aggregate Roots must be Auditable to comply with ISO 17025.
 */
public interface Auditable {
    String getReasonForChange();
    String getAnalystId();
}