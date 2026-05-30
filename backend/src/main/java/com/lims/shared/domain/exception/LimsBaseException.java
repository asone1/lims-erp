// 所屬路徑: com.lims.shared.domain.exception.LimsBaseException
package com.lims.shared.domain.exception;

import org.slf4j.MDC;
import java.util.HashMap;
import java.util.Map;

/**
 * 遵守: 領域層共用例外基底 (Business Exception)
 */
public class LimsBaseException extends RuntimeException {
    private final String businessCode; 
    private final String traceId;      // OTel Trace ID
    private final Map<String, Object> params = new HashMap<>();

    public LimsBaseException(String businessCode, String message) {
        super(message);
        this.businessCode = businessCode;
        // 從 OTel Context 或 MDC 獲取，若無則暫時標記
        String mdcTrace = MDC.get("traceId");
        this.traceId = mdcTrace != null ? mdcTrace : "NO-TRACE"; 
    }

    public String getBusinessCode() { return businessCode; }
    public String getTraceId() { return traceId; }
    public Map<String, Object> getParams() { return params; }

    public LimsBaseException addParam(String key, Object value) {
        this.params.put(key, value);
        return this; // 支援 Fluent API 串接寫法
    }
}