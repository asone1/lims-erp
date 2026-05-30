package com.lims.core.domain.sample;

import com.lims.shared.domain.*;
import com.lims.core.domain.exception.DomainConstraintViolationException; // 引入合規異常
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Immutable Aggregate Root: Sample.
 * Constitutional Principle: Failure Taxonomy (Constraint Validation).
 */
public record Sample(
    UUID sampleId,
    UUID orderId,
    String sampleType,
    String status,
    String analystId,
    String reasonForChange,
    String correlationId
) implements ConstitutionalAggregate {

    public Sample {
        // [治理修正]：拋出合規的領域異常，而非 IllegalArgumentException
        if (sampleId == null) {
            throw new DomainConstraintViolationException("sampleId", "GOV-SAMPLE-001");
        }
        if (orderId == null) {
            throw new DomainConstraintViolationException("orderId", "GOV-SAMPLE-002");
        }
    }

    // ... (其他實作保持不變)
    @Override public String getReasonForChange() { return reasonForChange; }
    @Override public String getAnalystId() { return analystId; }
    @Override public String getCorrelationId() { return correlationId; }
    
    @Override public Map<String, Object> toExportMap() { return Map.of("sampleId", sampleId, "orderId", orderId); }
    @Override public Map<String, Object> toReportData() { return Map.of("sampleId", sampleId, "status", status); }
    @Override public String getTemplateId() { return "TEMPLATE_SAMPLE_V1"; }
}