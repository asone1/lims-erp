package com.lims.core.domain.result.vo;

import com.lims.core.domain.exception.DomainConstraintViolationException;

/**
 * Value Object: Measurement
 * Constitutional Principle: Domain-Driven Design (Encapsulation).
 * Handles the logic of measurement representation, including precision and units.
 */
public record Measurement(
    String finalValue,   // 最終報告值 (String-based precision)
    String unit,         // 單位
    String rawValue      // 原始儀器讀數
) {
    public Measurement {
        // [治理實作]：防禦性編程，確保 ValueObject 不可變且數據完整
        if (finalValue == null || unit == null) {
            throw new DomainConstraintViolationException("finalValue/unit", "GOV-RESULT-101");
        }
    }
}