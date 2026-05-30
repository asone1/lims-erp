package com.lims.shared.dto;

import java.util.Map;
import java.time.Instant;

/**
 * Constitutional Principle: Versioned Runtime Contract
 * 前端透過 BusinessCode 查表翻譯，message 僅供開發階段除錯使用。
 */


public record ErrorResponse(
    String businessCode,
    String message,
    String traceId,
    Instant timestamp,
    Map<String, Object> params
) {}