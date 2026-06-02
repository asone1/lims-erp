// 所屬路徑: src/config/ui-label-config.js

// 1. 全域跨 Class 共用底層 (Base Config)
const COMMON_LABELS = {
  "orderId": { label: "系統內部識別碼" },
  "status": { label: "當前狀態" },
  "customerId": { label: "客戶" },
  "analystId": { label: "分析師代碼" },
  "orderDate": { label: "建立時間" },
  "reasonForChange": { label: "變更原因" },
  "correlationId": { label: "關聯追蹤碼" }
};

// 2. Order 專屬配置 (手動內聚，確保查表絕對命中)
const ORDER_LABELS = {
  ...COMMON_LABELS,
  "customerId": { label: "訂單客戶" }, // 覆寫
  "orderNo": { label: "訂單編號" }
};

// 3. Sample 專屬配置
const SAMPLE_LABELS = {
  ...COMMON_LABELS,
  "sampleNo": { label: "檢體編號" },
  "properties.bloodType": { 
    label: "檢體血型", 
    options: [
      { label: "A型", value: "A" },
      { label: "B型", value: "B" },
      { label: "O型", value: "O" },
      { label: "AB型", value: "AB" }
    ] 
  },
  "properties.fastingHours": { label: "空腹小時數" },
  "properties.source": { label: "採樣水源地" },
  "properties.phValue": { label: "預估酸鹼值 (pH)" }
};

const SCHEMA_CLASSES = {
  "ORDER": ORDER_LABELS,
  "SAMPLE": SAMPLE_LABELS
};

/**
 * 唯一的治理大腦：根據 Class 類別取得完整欄位 UI 設定
 */
export function getFieldUiConfig(classType, fieldName) {
  // 強制轉大寫，防止 'order' 與 'ORDER' 不匹配
  const normalizedClass = String(classType).toUpperCase();
  const classConfig = SCHEMA_CLASSES[normalizedClass];
  
  // 預設安全的備援物件
  const defaultCfg = { label: fieldName, options: [] };
  
  // 核心查表邏輯
  let finalFieldObj = null;
  
  if (classConfig && classConfig[fieldName]) {
    finalFieldObj = classConfig[fieldName];
  } else if (COMMON_LABELS[fieldName]) {
    finalFieldObj = COMMON_LABELS[fieldName];
  }
  
  // 100% 結構輸出防禦
  return {
    label: finalFieldObj && finalFieldObj.label ? finalFieldObj.label : fieldName,
    options: finalFieldObj && finalFieldObj.options ? finalFieldObj.options : []
  };
}