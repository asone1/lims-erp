package com.lims.governance.exception;

import com.lims.shared.dto.BaseErrorResponse;
import java.time.Instant;
import java.util.Map;

/**
 * Standardized error response for the Governance Plane.
 * Ensures consistent Failure Taxonomy across all services.
 */
public record GovernanceErrorResponse(
    String code,
    String message,
    String traceId,
    Instant timestamp,
    Map<String, Object> params
) implements BaseErrorResponse {}