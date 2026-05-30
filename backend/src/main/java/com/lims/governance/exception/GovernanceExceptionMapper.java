package com.lims.governance.exception;

import io.opentelemetry.api.trace.Span;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.Collections;

/**
 * Global Exception Mapper for Governance Violations.
 * Prevents internal system details from leaking to the client.
 */
@Provider
public class GovernanceExceptionMapper implements ExceptionMapper<GovernanceViolationException> {

    @Override
    public Response toResponse(GovernanceViolationException exception) {
        // Retrieve Trace ID from OTel Span
        String traceId = Span.current().getSpanContext().getTraceId();

        // Map internal exception to semantic error response
        GovernanceErrorResponse errorResponse = new GovernanceErrorResponse(
            exception.getErrorCode(),
            exception.getBusinessMessage(), // Semantic message for the UI
            traceId,
            Instant.now(),
            Collections.emptyMap()
        );

        // Failure Taxonomy: Governance violations are typically 403 Forbidden
        return Response.status(Response.Status.FORBIDDEN)
                       .entity(errorResponse)
                       .type("application/json")
                       .build();
    }
}