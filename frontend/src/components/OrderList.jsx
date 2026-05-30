// 所屬路徑: src/components/OrderList.jsx
import React, { useState, useEffect } from 'react';
import { OrderService } from '../services/OrderService';
import DynamicTable from './common/DynamicTable';

export default function OrderList({ orders, isLoading, onRefresh, setError }) {
    const [listSchema, setListSchema] = useState({ columns: [], actions: [] });

    useEffect(() => {
        // 遵守: 動態掛載 - 畫面載入時向後端請求 List Schema
        const fetchListSchema = async () => {
            try {
                const schema = await OrderService.getSchema('orders/list');
                setListSchema(schema);
            } catch (err) {
                setError('無法載入清單欄位設定。');
            }
        };
        fetchListSchema();
    }, [setError]);

    // 遵守: 行為意圖解析 (Action Intent Resolver)
    // 將後端 JSON 傳來的字串意圖，映射到真實的 JS 執行函式
    const executeAction = async (actionId, row) => {
        const operatorId = "OP-ANALYST-01";
        
        try {
            switch (actionId) {
                case 'ACCEPT_ORDER':
                    await OrderService.acceptOrder(row.orderNo.value, operatorId);
                    break;
                case 'CANCEL_ORDER':
                    const reason = prompt(`請輸入取消單號 [${row.orderNo.value}] 的治理原因:`);
                    if (!reason) return;
                    await OrderService.cancelOrder(row.orderId, reason, operatorId);
                    break;
                default:
                    console.warn(`未知的操作意圖: ${actionId}`);
                    return;
            }
            onRefresh(); // 執行成功後刷新列表
        } catch (err) {
            setError(`執行操作 [${actionId}] 失敗: ${err.message}`);
        }
    };

    // 將後端的 action definitions 轉譯為 DynamicTable 能夠理解的格式
    const parsedActions = listSchema.actions.map(actionDef => ({
        label: actionDef.label,
        danger: actionDef.danger,
        // 動態組裝狀態機防護邏輯
        showCondition: (row) => {
            if (actionDef.requiredStatuses && !actionDef.requiredStatuses.includes(row.status)) {
                return false; // 如果有指定必備狀態，且當前不在其中，則隱藏
            }
            if (actionDef.forbiddenStatuses && actionDef.forbiddenStatuses.includes(row.status)) {
                return false; // 如果有指定禁用狀態，且當前在其中，則隱藏
            }
            return true;
        },
        onClick: (row) => executeAction(actionDef.actionId, row)
    }));

    if (listSchema.columns.length === 0) return <p>正在載入資料表結構...</p>;

    return (
        <section className="order-list-section">
            <h2>所有訂單清單 (SDUI 後端驅動)</h2>
            <DynamicTable 
                columns={listSchema.columns} 
                data={orders} 
                rowActions={parsedActions} 
                isLoading={isLoading} 
            />
        </section>
    );
}