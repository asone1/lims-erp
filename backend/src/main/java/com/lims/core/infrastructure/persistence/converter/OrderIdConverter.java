// 所屬路徑: com.lims.core.infrastructure.persistence.converter.OrderIdConverter
package com.lims.core.infrastructure.persistence.converter;

import com.lims.core.domain.order.OrderId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 遵守: 基礎設施層 - 負責 Value Object 與資料庫原生型別的無縫轉換
 * autoApply = true 代表整個系統只要遇到 OrderId，就會自動套用此轉換邏輯
 */
@Converter(autoApply = true)
public class OrderIdConverter implements AttributeConverter<OrderId, String> {

    @Override
    public String convertToDatabaseColumn(OrderId attribute) {
        // Domain 寫入 DB: 轉為 String
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public OrderId convertToEntityAttribute(String dbData) {
        // DB 讀取至 Domain: 轉為 Value Object
        return dbData == null ? null : OrderId.of(dbData);
    }
}