package com.lims.core.domain.order.repository;

import com.lims.core.domain.order.Order;
import java.util.UUID;

/**
 * Constitutional Principle: CQRS (Command Side)
 * 此介面專職負責聚合根的變更與持久化，與 ReadOnlyRepository 嚴格隔離。
 */
public interface OrderCommandRepository {
    void save(Order order);
    void delete(UUID orderId);
}