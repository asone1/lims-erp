package com.lims.core.domain.schema.model;


/**
 * SchemaField 是 SDUI 的元數據核心。
 * 憲法原則：Governance Plane - 嚴格限制所有動態變更
 */
public record SchemaField(
    String fieldId,          // 唯一識別碼
    String version,          // Versioned Runtime Contract 版本
    String behaviorHash,     // 邏輯雜湊值，變更邏輯必須更換 Hash
    String displayName,      // UI 顯示名稱
    FieldType fieldType,     // 欄位類型 (STRING, NUMBER, DATE, etc.)
    FieldConstraint constraints, // 驗證規則 (Regex, Range, Required)
    String uiComponentType   // SDUI 渲染提示 (e.g., "InputField", "Dropdown")
) {
    public SchemaField {
        // 憲法級校驗：確保變更必有 Version 與 Hash 追蹤
        if (version == null || behaviorHash == null) {
            throw new IllegalStateException("Governance Violation: SchemaField must have Version and BehaviorHash.");
        }
    }
}

/**
 * 憲法原則：SDUI - 封裝驗證規則，防止前端硬編碼邏輯
 */
record FieldConstraint(
    boolean isRequired,
    String validationRegex,
    Double minValue,
    Double maxValue,
    String unit
) {}

enum FieldType {
    STRING, NUMBER, BOOLEAN, DATE_TIME, SELECT
}