// 所屬路徑: com.lims.web.exception.GlobalExceptionMapper
package com.lims.web.exception;
import com.lims.shared.dto.ErrorResponse;
import com.lims.shared.domain.exception.LimsBaseException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Instant;

/**
 * 遵守: 全域異常處理防護網 (Governance Error Handler)
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        String traceId = MDC.get("traceId") != null ? MDC.get("traceId") : "NO-TRACE";

        // 情況 A：已知業務異常 (Business Exception)
        if (exception instanceof LimsBaseException limsEx) {
            // 遵守: 業務異常記錄為 WARN，不印出長篇 Stack Trace
            LOG.warn("Business Exception [{}]: {} - TraceId: {}", 
                     limsEx.getBusinessCode(), limsEx.getMessage(), traceId);

            ErrorResponse payload = new ErrorResponse(
                limsEx.getBusinessCode(),
                limsEx.getMessage(),
                limsEx.getTraceId(),
                Instant.now(),
                limsEx.getParams()
            );

            return Response.status(Response.Status.BAD_REQUEST)
                           .type(MediaType.APPLICATION_JSON)
                           .entity(payload)
                           .build();
        }

        // 情況 B：未預期的系統異常 (System Error, 例如 NPE, 網路中斷, 資料庫連線失敗)
        // 遵守: 系統異常記錄為 ERROR，必須包含 Stack Trace 供工程師 Debug
        LOG.error("System Error encountered. TraceId: {}", traceId, exception);

        // 遵守: 絕對禁止將詳細系統堆疊外流給前端 (資安防禦)
        ErrorResponse payload = new ErrorResponse(
            "ERR-SYS-999",
            "An unexpected system error occurred. Please contact administrator.",
            traceId,
            Instant.now(),
            null
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .type(MediaType.APPLICATION_JSON)
                       .entity(payload)
                       .build();
    }
}