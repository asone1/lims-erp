package com.lims.core.domain.result;

import com.lims.core.domain.exception.DomainConstraintViolationException;
import com.lims.shared.domain.*;
import java.util.Map;
import java.util.UUID;
import com.lims.core.domain.result.vo.Measurement;
/**
 * Immutable Aggregate Root for ISO 17025 Test Results.
 * Constitutional Principle: Immutability & Trait-based Governance.
 */
public record TestResult(
    UUID resultId,
    UUID sampleId,
    String testMethodId,
    Measurement measurement, // [架構升級]：封裝測量值
    String status,
    String analystId,
    String reasonForChange,
    String correlationId
) implements ConstitutionalAggregate {

    public TestResult {
        if (resultId == null || sampleId == null) {
          throw new DomainConstraintViolationException("Missing mandatory identity (resultId or sampleId)");
        }
    }

    // Trait Implementation: Auditable
    @Override public String getReasonForChange() { return reasonForChange; }
    @Override public String getAnalystId() { return analystId; }

    // Trait Implementation: Idempotent
    @Override public String getCorrelationId() { return correlationId; }

    // Trait Implementation: Exportable/Reportable
    @Override public Map<String, Object> toExportMap() {
        return Map.of("resultId", resultId, "value", measurement.finalValue(), "unit", measurement.unit());
    }
    
    @Override public Map<String, Object> toReportData() {
        return Map.of("resultId", resultId, "status", status);
    }

    @Override public String getTemplateId() { return "template-test-result-001"; }
}