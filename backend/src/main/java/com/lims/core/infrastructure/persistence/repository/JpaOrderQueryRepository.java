package com.lims.core.infrastructure.persistence.repository;

import java.util.UUID;
import com.lims.core.domain.order.Order;
import com.lims.core.domain.order.OrderId;
import com.lims.core.domain.order.repository.OrderQuery;
import com.lims.core.domain.order.repository.OrderQueryRepository;
import com.lims.core.infrastructure.persistence.entity.OrderEntity;

import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.List;


@OrderQuery
@LookupIfProperty(name = "lims.db.mock", stringValue = "false", lookupIfMissing = true)
@ApplicationScoped
public class JpaOrderQueryRepository implements OrderQueryRepository {
    @Inject EntityManager em;

    @Override
    public Optional<Order> findById(UUID orderId) {
        // [憲法原則：ReadOnly] - 嚴格遵守查詢行為
        return Optional.ofNullable(em.find(OrderEntity.class, orderId))
                       .map(OrderEntity::toDomain);
    }
   // 1. OrderEntity 變回單純的 String
@Column(name = "order_no", nullable = false, unique = true, length = 32)
public String orderNo; 

// 2. Repository 直接傳入 unwrapped 的 String
@Override
public Optional<Order> findByOrderNo(OrderId orderNo) {
    // 直接將 Value Object 拆解為 String 丟給 Hibernate，絕對不會報錯
    return OrderEntity.find("orderNo", orderNo.getValue())
            .firstResultOptional()
            .map(entity -> ((OrderEntity) entity).toDomain());
}
    
    @Override
    public List<Order> findAll() {
        return em.createQuery("SELECT e FROM OrderEntity e", OrderEntity.class)
                 .getResultList()
                 .stream()
                 .map(OrderEntity::toDomain)
                 .toList();
    }

    @Override
    public boolean existsByCorrelationId(String correlationId) {
        return em.createQuery("SELECT 1 FROM OrderEntity e WHERE e.correlationId = :cid", Integer.class)
                 .setParameter("cid", correlationId)
                 .getResultList().size() > 0;
    }

  
}
