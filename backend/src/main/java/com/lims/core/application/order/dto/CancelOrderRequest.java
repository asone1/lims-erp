// 所屬路徑: com.lims.core.application.order.dto.CancelOrderRequest
package com.lims.core.application.order.dto;

/**
 * 遵守: 絕對禁止硬編碼，使用強型別合約規範輸入資料
 */
public record CancelOrderRequest(
    String reason
) {}