package com.lims.core.domain.sample;

import com.lims.shared.domain.repository.ReadOnlyRepository;
import java.util.List;
import java.util.UUID;

/**
 * Sample Repository Contract.
 * Constitutional Principle: DDD Isolation.
 */
public interface SampleRepository extends ReadOnlyRepository<Sample, UUID> {

    /**
     * Persists the Sample aggregate.
     */
    void save(Sample sample);

    /**
     * Query method: Fetch all samples belonging to a specific order.
     * Note: This keeps the Bounded Context of Sample intact.
     */
    List<Sample> findByOrderId(UUID orderId);

    /**
     * Idempotency check.
     */
    boolean existsByCorrelationId(String correlationId);
}