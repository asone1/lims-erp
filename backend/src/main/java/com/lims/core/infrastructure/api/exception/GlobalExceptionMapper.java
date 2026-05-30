package com.lims.core.infrastructure.api.exception;

import com.lims.shared.dto.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper; // 這就是你需要 import 的介面
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import java.time.Instant;
import com.lims.shared.domain.exception.LimsBaseException;
/**
 * Constitutional Principle: Failure Taxonomy
 * Responsibility: Final safety net to intercept unhandled exceptions and translate them into business contracts.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        String traceId = MDC.get("traceId");

        // Category 1: Domain/Business Errors
        if (exception instanceof LimsBaseException baseEx) {
            // Governance Rule: Expected business errors are logged as WARN and do not trigger on-call alerts.
            LOG.warn("Business error detected. Code: {}, TraceId: {}, Message: {}", 
                     baseEx.getBusinessCode(), traceId, baseEx.getMessage());
            
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(
                    baseEx.getBusinessCode(), // 對應到 ErrorResponse 的第一個參數 'code'
                    baseEx.getMessage(), 
                    traceId,
                    Instant.now(),
                    baseEx.getParams()
                )).build();
        }

        // Category 2: System Panic (Unexpected Errors)
        // Constitutional Principle: Prohibit leaking stack traces to the frontend to protect underlying schema security.
        LOG.error("[System Panic] TraceId: {}", traceId, exception); 

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(new ErrorResponse(
                "ERR-SYS-999", 
                "An unexpected internal error occurred. Please contact the administrator.", 
                traceId,
                Instant.now(),
                null
            )).build();
    }
}