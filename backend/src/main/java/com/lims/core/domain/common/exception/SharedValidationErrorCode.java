// 所屬路徑: com.lims.core.domain.common.exception.SharedValidationErrorCode
package com.lims.core.domain.common.exception;

/**
 * 遵守: Shared Kernel (共用核心) - 跨領域的基礎欄位與狀態驗證
 * 適用於 Order, Sample, TestReport 等所有核心聚合根
 */
public enum SharedValidationErrorCode {
    
    // 基礎欄位驗證 (只需傳入: 欄位名稱)
    FIELD_REQUIRED("DOM-VAL-001", "Validation Block: Field [%s] is strictly required."),
    FIELD_FORMAT_INVALID("DOM-VAL-002", "Validation Block: Field [%s] format [%s] is invalid."),
    
    // 狀態機共用驗證 (只需傳入: 實體名稱, 當前狀態, 目標狀態)
    STATE_TRANSITION_DENIED("DOM-STAT-001", "State Machine Violation: [%s] cannot transition from [%s] to [%s].");

    private final String code;
    private final String messageTemplate;

    SharedValidationErrorCode(String code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    public String getCode() { return code; }
    
    public String formatMessage(Object... args) { 
        return String.format(messageTemplate, args); 
    }
}