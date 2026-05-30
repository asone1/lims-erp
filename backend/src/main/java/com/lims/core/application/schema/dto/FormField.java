// 所屬路徑: com.lims.core.application.schema.dto.FormField
package com.lims.core.application.schema.dto;

import java.util.List;

/**
 * 遵守: SDUI 規範 - 將前端的 UI 渲染與驗證邏輯抽象為後端 DTO
 */
public record FormField(
    String key,           // 對應前端資料的綁定鍵值 (支援巢狀，如 properties.type)
    String label,         // 顯示標籤
    String type,          // 欄位型態: text, number, select, date
    boolean required,     // 對齊後端 Command/Validator 的必填規則
    List<Option> options  // 下拉選單選項 (若無則為 null)
) {
    public record Option(String label, String value) {}
}