package com.lims.core.domain.order;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.Map;
import java.util.UUID; // 這是 Java 內建的類別

@ApplicationScoped
public class OrderFactory {

    public Order createInitialOrder(
            OrderId orderNo, // Service 傳進來的業務 ID (LIMS-...)
            String customerId, 
            String analystId, 
            Map<String, Object> properties) {
        
        // 遵守: 嚴格按照 record 的 9 個參數順序填入
        return new Order(
            UUID.randomUUID(),     // 1. id: 工廠自己產生系統 UUID！解決你的報錯！
            orderNo,               // 2. orderNo: 填入 Service 傳來的業務 ID
            customerId,            // 3. customerId
            "DRAFT",               // 4. status: 初始狀態
            analystId,             // 5. analystId
            "System Initial Creation", // 6. reasonForChange
            UUID.randomUUID().toString(), // 7. correlationId (追蹤碼也可以自己產生)
            Instant.now(),         // 8. orderDate: 當前時間
            properties             // 9. properties
        );
    }
}