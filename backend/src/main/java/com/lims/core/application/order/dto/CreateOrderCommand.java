// 所屬路徑: com.lims.core.application.order.dto.CreateOrderCommand
package com.lims.core.application.order.dto;

import java.util.Map;

/**
 * 遵守: CQRS Command 模式 - 封裝前端建立訂單的意圖 (DTO)
 * 這裡絕對沒有 UUID 或 OrderIdentity
 */
public record CreateOrderCommand(
    String customerId,
    String analystId,
    Map<String, Object> properties // 遵守: SDUI 動態擴展欄位
) {
    public CreateOrderCommand {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("客戶代碼不可為空");
        }
    }
}