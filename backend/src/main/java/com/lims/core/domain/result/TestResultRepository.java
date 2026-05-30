package com.lims.core.domain.result;

import java.util.Optional;
import java.util.UUID;

import com.lims.shared.domain.repository.ReadOnlyRepository;
import com.lims.shared.domain.repository.Repository;

/**
 * Repository interface for TestResult Aggregate.
 * Constitutional Principle: DDD Isolation.
 * Domain Layer defines the contract, Infrastructure Layer implements the persistence.
 */
public interface TestResultRepository extends ReadOnlyRepository<TestResult, UUID>, Repository<TestResult, UUID>{

    /**
     * Persists the TestResult. 
     * In an Event-Driven architecture, this implementation MUST handle
     * the Transactional Outbox pattern to ensure atomicity between 
     * data persistence and event emission.
     */
    void save(TestResult result);

    /**
     * Retrieves the aggregate by ID.
     */
    Optional<TestResult> findById(UUID resultId);

    /**
     * Used for Idempotency validation.
     */
    boolean existsByCorrelationId(String correlationId);
}