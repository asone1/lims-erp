/**
 * 統一產生符合後端檢驗的 LIMS 基礎 Headers
 * 動態讀取 .env 檔案中注入的環境變數
 */
export const LIMS_HEADERS = (customHeaders = {}) => {
    return {
        'X-LIMS-Field-ID': import.meta.env.VITE_LIMS_FIELD_ID ,
        'X-LIMS-Input-Version': import.meta.env.VITE_LIMS_INPUT_VERSION,
        'X-LIMS-Output-Version': import.meta.env.VITE_LIMS_OUTPUT_VERSION ,
        'X-LIMS-Behavior-Hash': import.meta.env.VITE_LIMS_BEHAVIOR_HASH,
        'Accept': 'application/json',
        ...customHeaders
    };
};