package com.lims.core.domain.order.repository;

import com.lims.core.domain.order.Order;
import com.lims.core.domain.order.OrderId;
import com.lims.shared.domain.repository.ReadOnlyRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

/**
 * Order Repository Contract.
 * Constitutional Principle: DDD Isolation.
 * Domain Layer defines the contract, Infrastructure Layer implements it.
 */
public interface OrderQueryRepository extends ReadOnlyRepository<Order, UUID> {


    /**
     * Idempotency check.
     */
    boolean existsByCorrelationId(String correlationId);

    
    List<Order> findAll();
    
    Optional<Order> findByOrderNo(OrderId orderNo);
}