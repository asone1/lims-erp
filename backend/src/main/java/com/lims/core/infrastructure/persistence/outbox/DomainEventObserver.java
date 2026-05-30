package com.lims.core.infrastructure.persistence.outbox;

import com.lims.core.domain.order.event.OrderCreatedEvent;
import com.lims.core.infrastructure.persistence.entity.OutboxEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

/**
 * 統一的領域事件觀察者 (Unified Domain Event Observer)
 * 職責：捕捉所有系統內的 CDI 事件，並將其持久化至 Transactional Outbox。
 */
@ApplicationScoped
public class DomainEventObserver { 

    private static final Logger LOG = LoggerFactory.getLogger(DomainEventObserver.class);

    @Inject
    EntityManager em;

    @Inject
    ObjectMapper objectMapper; // Jackson 用於標準序列化

    /**
     * 處理訂單創建事件
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void onOrderCreated(@Observes OrderCreatedEvent event) {
        persistEvent("OrderCreated", event.order());
    }

    /**
     * 預留給 Sample 事件 (當你建立 SampleCreatedEvent 時取消註解)
     */
    /*
    @Transactional(Transactional.TxType.REQUIRED)
    public void onSampleCreated(@Observes SampleCreatedEvent event) {
        persistEvent("SampleCreated", event.sample());
    }
    */

    private void persistEvent(String type, Object domainObject) {
        try {
            LOG.info("Domain event detected: [{}]. Persisting to outbox. State: {}", type, domainObject);

            OutboxEntity outbox = new OutboxEntity();
            outbox.eventId = UUID.randomUUID();
            outbox.eventType = type;
            
            // 使用 Jackson 將整個 Domain Record 序列化為 JSON，確保資料完整性
            outbox.payload = objectMapper.writeValueAsString(domainObject);
            
            outbox.createdAt = Instant.now();
            outbox.status = "PENDING";

            em.persist(outbox);
        } catch (Exception e) {
            LOG.error("Failed to persist event to Outbox. Type: {}", type, e);
            // Note: Exception is swallowed to prevent Outbox failure from rolling back main business transaction.
        }
    }
}