package com.lims.core.infrastructure.persistence.entity;

import com.lims.core.domain.sample.Sample;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * Persistence Entity for Sample.
 * Constitutional Principle: DDD Isolation (Infrastructure Layer).
 * This class is strictly for DB mapping and must not contain domain business logic.
 */
@Entity
@Table(name = "lims_samples")
public class SampleEntity {

    @Id
    @Column(name = "sample_id", nullable = false)
    public UUID sampleId;

    @Column(name = "order_id", nullable = false)
    public UUID orderId;

    @Column(name = "sample_type")
    public String sampleType;

    @Column(name = "status")
    public String status;

    @Column(name = "analyst_id")
    public String analystId;

    @Column(name = "reason_for_change")
    public String reasonForChange;

    @Column(name = "correlation_id")
    public String correlationId;

    /**
     * Mapper Method: Converts Persistence Entity to Domain Object.
     * Constitutional Principle: Data Layer Transparency.
     */
    public Sample toDomain() {
        return new Sample(
            this.sampleId,
            this.orderId,
            this.sampleType,
            this.status,
            this.analystId,
            this.reasonForChange,
            this.correlationId
        );
    }

    /**
     * Static Mapper: Creates Entity from Domain Object.
     */
    public static SampleEntity fromDomain(Sample sample) {
        SampleEntity entity = new SampleEntity();
        entity.sampleId = sample.sampleId();
        entity.orderId = sample.orderId();
        entity.sampleType = sample.sampleType();
        entity.status = sample.status();
        entity.analystId = sample.analystId();
        entity.reasonForChange = sample.reasonForChange();
        entity.correlationId = sample.correlationId();
        return entity;
    }
}