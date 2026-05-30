package com.lims.core.infrastructure.persistence.entity;
import com.lims.core.domain.result.vo.Measurement;
import com.lims.core.domain.result.TestResult;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * Persistence Entity for TestResult.
 * Constitutional Principle: Domain-Infrastructure Synchronization.
 */
@Entity
@Table(name = "lims_test_results")
public class TestResultEntity {

    @Id
    @Column(name = "result_id", nullable = false)
    public UUID resultId;

    @Column(name = "sample_id", nullable = false)
    public UUID sampleId;

    @Column(name = "test_method_id")
    public String testMethodId;

    @Column(name = "test_value") // 對應 record 中的 value
    public String value;


    @Column(name = "raw_value", columnDefinition = "TEXT") 
    public String rawValue;

    // ... Mapper 映射邏輯同步修正
    @Column(name = "unit")
    public String unit;

    @Column(name = "status")
    public String status;

    @Column(name = "analyst_id")
    public String analystId;

    @Column(name = "reason_for_change")
    public String reasonForChange;

    @Column(name = "correlation_id")
    public String correlationId;

    /**
     * Mapper: Domain -> Entity
     * Constitutional Principle: Strict Contract Alignment.
     */
    public static TestResultEntity fromDomain(TestResult domain) {
        TestResultEntity entity = new TestResultEntity();
        entity.resultId = domain.resultId();
        entity.sampleId = domain.sampleId();
        entity.testMethodId = domain.testMethodId();
        entity.value = domain.measurement().finalValue();
        entity.unit = domain.measurement().unit();
        entity.status = domain.status();
        entity.analystId = domain.analystId();
        entity.reasonForChange = domain.reasonForChange();
        entity.correlationId = domain.correlationId();
        return entity;
    }

    /**
     * Mapper: Entity -> Domain
     */
   /**
     * Constitutional Principle: Domain-Driven Design (Encapsulation).
     * Maps flat entity fields into the Measurement Value Object before domain construction.
     */
    public TestResult toDomain() {
        Measurement measurement = new Measurement(
            this.value != null ? String.valueOf(this.value) : "0", 
            this.unit,
            this.rawValue 
        );

        return new TestResult(
            this.resultId, 
            this.sampleId, 
            this.testMethodId, 
            measurement, 
            this.status, 
            this.analystId, 
            this.reasonForChange, 
            this.correlationId
        );
    }
}