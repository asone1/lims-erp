package com.lims.shared.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Common contract for all API error responses.
 * Ensures consistent Failure Taxonomy across Domain and Governance planes.
 */
public interface BaseErrorResponse {
    String code();
    String message();
    String traceId();
    Instant timestamp();
    Map<String, Object> params();
}