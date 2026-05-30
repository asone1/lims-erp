package com.lims.shared.reporting;

import com.lims.shared.domain.Reportable;

/**
 * The Engine responsible for merging Data (from Reportable) and Template.
 * Must be implemented by Infrastructure layer to ensure proper decoupling.
 */
public interface ReportEngine {
    
    /**
     * Generates HTML string.
     * Constitutional Principle: Fail Gracefully. If template is missing,
     * throw TemplateNotFoundException.
     */
    String generateHtml(Reportable data);
}