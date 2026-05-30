package com.lims.core.infrastructure.persistence.mapper;

import com.lims.core.domain.order.Order;
import com.lims.core.domain.order.OrderId;
import com.lims.core.infrastructure.persistence.entity.OrderEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderMapper {

    // 將 Domain 轉為 Entity (Persistence)
    public OrderEntity fromDomain(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.orderNo = order.orderNo().getValue();
        entity.customerId = order.customerId();
        entity.status = order.status();
        entity.analystId = order.analystId();
        entity.correlationId = order.correlationId();
        entity.orderDate = order.orderDate();
        entity.reasonForChange = order.reasonForChange();
        entity.properties = order.properties();
        
        // 憲法原則：Versioned Runtime Contract 檢查點
        // 此處應由治理層攔截，確保 Behavior Hash 符合當前 Schema 版本
        return entity;
    }

    // 將 Entity 轉為 Domain (Core)
    public Order toDomain(OrderEntity entity) {
        return new Order(
            entity.orderId,
            OrderId.of(entity.orderNo), // 補上遺漏的業務自然鍵
            entity.customerId,
            entity.status,
            entity.analystId,
            entity.reasonForChange != null ? entity.reasonForChange : "N/A",
            entity.correlationId,
            entity.orderDate,
            entity.properties
        );
    }
}