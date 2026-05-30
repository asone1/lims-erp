package com.lims.core.infrastructure.persistence.repository;

import com.lims.core.domain.result.TestResult;
import com.lims.core.domain.result.TestResultRepository;
import com.lims.core.domain.result.event.TestResultCreated;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.core.infrastructure.persistence.entity.OutboxEntity;
import com.lims.core.infrastructure.persistence.entity.TestResultEntity;
import java.time.Instant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
@ApplicationScoped
public class JpaTestResultRepository implements TestResultRepository {

   @Inject
    EntityManager em;

   @Inject
    ObjectMapper objectMapper;

   @Override
    @Transactional
    public void save(TestResult result) {
        // 1. 持久化聚合根 (Aggregate Root)
        TestResultEntity entity = TestResultEntity.fromDomain(result);
        em.persist(entity);

        // 2. 建立 Business Event
       TestResultCreated event = new TestResultCreated(
        UUID.randomUUID(),
        result.resultId(),
        result.sampleId(),       // 從 Domain Aggregate 提取
        result.analystId(),      // 從 Domain Aggregate 提取
        result.correlationId(),
        Instant.now()
    );
        // 3. 持久化事件至 Outbox (Transactional Outbox Pattern)
        OutboxEntity outbox = new OutboxEntity();
        outbox.eventId = event.getEventId();
        outbox.eventType = event.getVersion();
        outbox.payload = serialize(event); // 使用 JSON 序列化工具
        outbox.createdAt = Instant.now();
        outbox.status = "PENDING";
        
        em.persist(outbox);
        
        // [憲法原則]：兩者在同一 @Transactional 區塊內，若 persist 失敗，兩者皆不寫入。
    }
    
    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize domain event", e);
        }
    }
    @Override
    public Optional<TestResult> findById(UUID resultId) {
        return Optional.ofNullable(em.find(TestResultEntity.class, resultId))
                       .map(TestResultEntity::toDomain);
    }
    
    @Override
    public boolean existsByCorrelationId(String correlationId) {
        return em.createQuery("SELECT COUNT(e) FROM TestResultEntity e WHERE e.correlationId = :cid", Long.class)
                 .setParameter("cid", correlationId)
                 .getSingleResult() > 0;
    }
}