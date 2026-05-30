// 遵守: 集中管理 API 路徑，配合 API Gateway 路由
const API_BASE_URL = '/api/orders';
const SCHEMA_API_URL = '/api/schemas';
/**
 * 訂單服務層：負責與 Quarkus 後端進行資料交換
 * 遵守: 絕對禁止硬編碼 (No Hardcoding) - UI 呈現與驗證應依賴後端 Schema，此處僅做單純的資料傳輸層 (DTO 傳遞)
 */
export const OrderService = {
    /**
     * 遵守: SDUI - 向治理層取得版面契約
     * @param {string} schemaPath - 例如 'orders/create' 或 'orders/list'
     */
    async getSchema(schemaPath) {
        try {
            const response = await fetch(`${SCHEMA_API_URL}/${schemaPath}`);
            if (!response.ok) {
                throw new Error(`獲取 Schema 失敗: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error(`獲取 [${schemaPath}] Schema 發生異常:`, error);
            throw error; // 拋出給 UI 顯示錯誤
        }
    },
    
    async getAllOrders() {
        try {
            const response = await fetch(API_BASE_URL);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            // 遵守: 預期失敗 (Design for Failure) - 容錯處理，避免畫面崩潰
            console.error("無法獲取訂單列表:", error);
            return []; 
        }
    },

    async createOrder(orderData) {
        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                // 遵守: SDUI 規範 - orderData 內部的 properties 必須由後端 Schema 定義，前端不寫死欄位
                body: JSON.stringify(orderData),
            });
            
            if (!response.ok) {
                // 預期後端會回傳包含 TraceId 與 LimsBaseException 的 JSON 格式錯誤
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `建立訂單失敗 (狀態碼: ${response.status})`);
            }
            return await response.json();
        } catch (error) {
            console.error("建立訂單發生異常:", error);
            throw error;
        }
    },

    /**
     * 接受訂單 (狀態機推進)
     * 遵守: CQRS 與事件驅動 - 寫入操作，觸發 Business Event
     * @param {string} orderNo - 業務自然鍵 (如 LIMS-2026...)
     * @param {string} operatorId - 操作員 ID
     */
    async acceptOrder(orderNo, operatorId) {
        try {
            const response = await fetch(`${API_BASE_URL}/${orderNo}/accept`, {
                method: 'PATCH', // 使用 PATCH 語意代表部分狀態更新
                headers: {
                    'Content-Type': 'application/json',
                    'X-Operator-Id': operatorId // 遵守: 操作員追蹤，透過 Header 傳遞避免污染 Payload
                }
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `無法接受訂單 ${orderNo}`);
            }
        } catch (error) {
            console.error(`接受訂單 [${orderNo}] 發生異常:`, error);
            throw error;
        }
    },

    /**
     * 取消/刪除訂單 (邏輯刪除)
     * 遵守: 憲法原則 - 狀態變更必須紀錄原因與操作者
     * @param {string} orderId - 系統代理鍵 (UUID)
     * @param {string} reason - 取消原因
     * @param {string} operatorId - 操作員 ID
     */
    async cancelOrder(orderId, reason, operatorId) {
        try {
            const response = await fetch(`${API_BASE_URL}/${orderId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Operator-Id': operatorId
                },
                body: JSON.stringify({ reason: reason }) // 遵守: 業務規則，必須附帶原因
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `無法取消訂單 ${orderId}`);
            }
        } catch (error) {
            console.error(`取消訂單 UUID [${orderId}] 發生異常:`, error);
            throw error;
        }
    }
};