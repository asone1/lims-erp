package com.lims.core.domain.order;

import com.lims.shared.domain.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Immutable Aggregate Root: Order.
 * Constitutional Principle: Aggregate Boundary (Top-level).
 */
public record Order(
    UUID orderId,                  // 系統代理鍵 (絕對不可變，資料庫 PK，微服務關聯憑證)
    OrderId orderNo,               // 業務自然鍵 (中央分派，如 LIMS-2026...)
    String customerId,             
    String status,
    String analystId,
    String reasonForChange,
    String correlationId,
    Instant orderDate,
    Map<String, Object> properties // 遵守: SDUI 動態擴充屬性
) implements ConstitutionalAggregate {

    public Order {
        // 遵守: 憲法原則 - 聚合邊界雙 ID 驗證
        if (orderId == null || orderNo == null || customerId == null) {
            throw new IllegalArgumentException("Governance Violation: Order must have system identity, business identity, and customerId.");
        }
    }

    @Override public String getReasonForChange() { return reasonForChange; }
    @Override public String getAnalystId() { return analystId; }
    @Override public String getCorrelationId() { return correlationId; }

    @Override public Map<String, Object> toExportMap() {
        // 建議匯出時，除了 UUID 也要帶上業務 ID orderNo
        return Map.of("orderId", orderId, "orderNo", orderNo.getValue(), "customerId", customerId, "status", status);
    }
    
    @Override public Map<String, Object> toReportData() {
        return Map.of("orderId", orderId, "orderNo", orderNo.getValue(), "status", status);
    }

    @Override public String getTemplateId() { return "TEMPLATE_ORDER_V1"; }

    /**
     * 遵守: DDD 豐富領域模型 - 狀態轉換：接受訂單
     */
    public Order accept(String operatorId) {
        // 1. 狀態機防護：只有 DRAFT (草稿) 或 PENDING (待處理) 可以被接受
        if (!"DRAFT".equals(this.status) && !"PENDING".equals(this.status)) {
            throw new IllegalStateException(
                String.format("State Machine Violation: Cannot accept order [%s] from state [%s]", this.orderNo.getValue(), this.status)
            );
        }

        // 2. 產生新的狀態快照 (嚴格對齊 9 個參數)
        return new Order(
            this.orderId,
            this.orderNo,        // 補上漏掉的業務自然鍵
            this.customerId,
            "ACCEPTED",          // 更新狀態
            operatorId,          // 紀錄是哪位分析師接受的
            "Order Accepted",    // 紀錄原因
            this.correlationId,
            this.orderDate,
            this.properties
        );
    }

    /**
     * 遵守: DDD 豐富領域模型 - 邏輯刪除：轉換狀態並記錄原因
     */
    public Order cancel(String reason, String operatorId) {
        // 1. 狀態機防護：已經完成或取消的訂單無法再次取消
        if ("COMPLETED".equals(this.status) || "CANCELLED".equals(this.status)) {
            throw new IllegalStateException(
                String.format("State Machine Violation: Cannot cancel order [%s] from state [%s]", this.orderNo.getValue(), this.status)
            );
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Governance Violation: Cancellation must include a reason.");
        }

        // 2. 產生新的狀態快照 (嚴格對齊 9 個參數)
        return new Order(
            this.orderId,
            this.orderNo,        // 補上漏掉的業務自然鍵
            this.customerId,
            "CANCELLED",         // 更新狀態
            operatorId,          // 紀錄是哪位分析師取消的
            reason,              // 寫入取消原因
            this.correlationId,
            this.orderDate,
            this.properties
        );
    }
}