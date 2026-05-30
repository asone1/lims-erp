package com.lims.core.infrastructure.persistence.repository;

import com.lims.core.domain.order.Order;
import com.lims.core.domain.order.repository.OrderCommand;
import com.lims.core.domain.order.repository.OrderCommandRepository;
import com.lims.core.infrastructure.persistence.entity.OrderEntity;

import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.UUID;

@OrderCommand
@LookupIfProperty(name = "lims.db.mock", stringValue = "false", lookupIfMissing = true)
@ApplicationScoped
public class JpaOrderCommandRepository implements OrderCommandRepository {

    @Inject
    EntityManager em;

    @Override
    @Transactional
    public void save(Order order) {
        // fromDomain now ensures orderId is never null
        OrderEntity entity = OrderEntity.fromDomain(order);
        
        if (em.find(OrderEntity.class, entity.orderId) != null) {
            em.merge(entity);
        } else {
            em.persist(entity);
        }
    }

    @Override
    @Transactional
    public void delete(UUID orderId) {
        // 先尋找實體，存在則刪除
        // 在邏輯刪除模式下，delete 方法實際上是更新狀態
        OrderEntity entity = em.find(OrderEntity.class, orderId);
        if (entity != null) {
            entity.status = "CANCELLED";
            entity.reasonForChange = "Soft delete requested via repository";
        }
    }
}