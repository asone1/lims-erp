package com.lims.core.domain.order;

import com.lims.core.domain.common.exception.SharedValidationErrorCode;
import com.lims.core.domain.common.id.Identity;
import com.lims.core.domain.common.id.PrefixedId;
import com.lims.shared.domain.exception.LimsBaseException; // 沿用全域異常

/**
 * 遵守: DDD 封裝邊界 - 屬於 LIMS 核心域的訂單唯一標識
 */
@PrefixedId(prefix = "LIMS", sequenceLength = 6)
public final class OrderId implements Identity {
    
    private final String value;

    public static final String PREFIX = "LIMS";
    public static final int SEQ_LENGTH = 6;
    
    // 給 Dispatcher 用的組裝模板: "%s-%s-%06d" -> "LIMS-20260525-000001"
    public static final String GENERATION_TEMPLATE = "%s-%s-%0" + SEQ_LENGTH + "d"; 
    
    // 給自己驗證用的正則表達式: "^LIMS-\d{8}-\d{6}$"
    public static final String VALIDATION_REGEX = "^" + PREFIX + "-\\d{8}-\\d{" + SEQ_LENGTH + "}$";


    private OrderId(String value) {
        // 驗證規則可透過反射讀取註解動態生成，此處保留核心邏輯
        if (value == null || !value.matches("^LIMS-\\d{8}-\\d{6}$")) {
            // 共用 Enum 威力：只要告訴它哪個欄位、什麼格式錯了
            throw new LimsBaseException(
                SharedValidationErrorCode.FIELD_FORMAT_INVALID.getCode(),
                SharedValidationErrorCode.FIELD_FORMAT_INVALID.formatMessage("OrderIdentity", value)
            );
        }
        this.value = value;
    }

    public static OrderId of(String value) {
        return new OrderId(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof OrderId orderId) { // Java 17 Pattern matching
            return this.value.equals(orderId.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}