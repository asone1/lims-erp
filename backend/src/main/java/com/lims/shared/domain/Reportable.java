package com.lims.shared.domain;

import java.util.Map;

/**
 * Constitutional Principle: Schema-Driven Reporting.
 * Objects implementing this interface provide the data required to populate
 * a Report Template.
 */
public interface Reportable {

    /**
     * Returns the data payload for the report.
     * The keys must align with the SchemaRegistry definitions.
     */
    Map<String, Object> toReportData();

    /**
     * Identifies which HTML template to use (e.g., "lab-result-template-v1").
     */
    String getTemplateId();
}