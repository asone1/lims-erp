package com.lims.governance.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.opentelemetry.api.trace.Span;

@Provider
public class GovernanceExceptionHandler implements ExceptionMapper<GovernanceViolationException> {

    private static final Logger LOG = LoggerFactory.getLogger(GovernanceExceptionHandler.class);

    @Override
    public Response toResponse(GovernanceViolationException exception) {
        String traceId = Span.current().getSpanContext().getTraceId();

        LOG.error("Governance Violation detected. TraceId: {}. Violation: {}", traceId, exception.getErrorCode());

        GovernanceErrorResponse errorResponse = new GovernanceErrorResponse(
            exception.getErrorCode(),
            "Governance Policy Enforcement: Request rejected by business contract rules.",
            traceId,
            Instant.now(),
            Collections.singletonMap("technical_reason", exception.getMessage())
        );

        return Response.status(Response.Status.FORBIDDEN)
                       .entity(errorResponse)
                       .build();
    }
}