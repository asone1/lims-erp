package com.lims.core.infrastructure.persistence.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.lims.core.domain.order.Order;
import com.lims.core.domain.order.OrderId; // 引入你的業務 ID Value Object
import com.lims.core.infrastructure.persistence.converter.OrderIdConverter;

/**
 * 遵守: 基礎設施層 (Infrastructure) - 僅負責與資料庫表結構映射，無業務邏輯
 */
@Entity
@Table(name = "lims_orders")
public class OrderEntity extends PanacheEntityBase {
    
    @Id
    @Column(name = "order_id", nullable = false)
    public UUID orderId; // 系統代理鍵 (PK)

    
    @Column(name = "order_no", nullable = false, unique = true, length = 32)
    public String orderNo; 

    public String customerId;
    public String status;
    public String analystId;
    public String correlationId;
    public Instant orderDate;
    
    @Column(name = "reason_for_change")
    public String reasonForChange;
    
    // 遵守: SDUI 動態 Schema 落地 - 支援 JSON 彈性欄位
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "properties", columnDefinition = "json")
    public Map<String, Object> properties;

    /**
     * Mapper Method: Infrastructure 轉換為 Domain
     * 負責將 DB 弱型別 (String) 昇華為 Domain 強型別 (OrderId)
     */
    public Order toDomain() {
        return new Order(
            this.orderId, 
            OrderId.of(this.orderNo), // 轉回 Value Object
            this.customerId, 
            this.status, 
            this.analystId, 
            this.reasonForChange != null ? this.reasonForChange : "N/A", 
            this.correlationId, 
            this.orderDate, 
            this.properties
        );
    }

    /**
     * Mapper Method: Domain 轉換為 Infrastructure Entity
     * 負責將 Domain 拆解為 DB 可接受的扁平結構
     */
    public static OrderEntity fromDomain(Order order) {
        OrderEntity entity = new OrderEntity();
        
        // 遵守: 領域絕對信任 (Domain Trust) 
        // ID 統一由 OrderFactory 分派，Entity 嚴禁自行 fallback 產生 UUID
        entity.orderId = order.orderId(); 
        entity.orderNo = order.orderNo().getValue(); // 將 Value Object 拆解為字串
        
        entity.customerId = order.customerId();
        entity.status = order.status();
        entity.analystId = order.analystId();
        entity.correlationId = order.correlationId();
        entity.orderDate = order.orderDate();
        entity.reasonForChange = order.reasonForChange();
        entity.properties = order.properties();
        
        return entity;
    }
}