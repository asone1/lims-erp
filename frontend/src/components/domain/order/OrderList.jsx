// 所屬路徑: src/components/domain/order/OrderList.jsx
import React, { useState, useEffect } from 'react';
import { OrderService } from './OrderService';
import DynamicTable from '@common/DynamicTable';
import { getFieldUiConfig } from '@config/ui-label-config';

export default function OrderList({ orders, isLoading, onRefresh, setError, templateType = "BLOOD" }) {
    const [listSchema, setListSchema] = useState({ columns: [], actions: [] });

    useEffect(() => {
        const fetchListSchema = async () => {
            try {
                // 後端回傳格式 e.g. [ { key: "customerId", label: "客戶代碼", type: "text", required: true, options: null }, ... ]
                const rawSchemaArray = await OrderService.getSchema(`orders/list?templateType=${templateType}`);

                // 遵守 SDUI 的前端裝飾原則：將後端的結構結合前端配置轉譯為 UI 元件認識的格式
                // 請將 src/components/domain/order/OrderList.jsx 裡面的 rawSchemaArray.map 修改為：

                const mappedColumns = rawSchemaArray.map(field => {
                    const actualKey = field.key || field.fieldName;
                    if (!actualKey) return null;

                    // 去前端配置檔查表
                    const targetClass = actualKey.startsWith('properties.') ? "SAMPLE" : "ORDER";
                    const uiConfig = getFieldUiConfig(targetClass, actualKey);

                    // 絕對權重決策鏈決定標籤文字
                    let finalLabel = "";
                    if (uiConfig && uiConfig.label && uiConfig.label !== actualKey) {
                        finalLabel = uiConfig.label;
                    } else if (field.label) {
                        finalLabel = field.label;
                    } else {
                        finalLabel = actualKey;
                    }

                    return {
                        dataIndex: actualKey,     // 💡 核心亮點：放棄使用 key 命名，改用安全的 dataIndex 傳遞路徑
                        label: finalLabel,        // 客製化中文標籤
                        type: field.type,
                        required: field.required,
                        customRender: actualKey === 'orderNo'
                            ? (val) => val?.value || val
                            : undefined
                    };
                }).filter(Boolean);

                // 註：若後端暫未實現 actions 接口，先給予預設值防禦
                setListSchema({
                    columns: mappedColumns,
                    actions: [
                        { actionId: 'ACCEPT_ORDER', label: '接受訂單', requiredStatuses: ['DRAFT', 'PENDING'] },
                        { actionId: 'CANCEL_ORDER', label: '取消訂單', danger: true, forbiddenStatuses: ['COMPLETED', 'CANCELLED'] }
                    ]
                });
            } catch (err) {
                setError('無法載入清單欄位設定。');
            }
        };
        fetchListSchema();
    }, [setError, templateType]);

    const executeAction = async (actionId, row) => {
        const operatorId = "OP-ANALYST-01";
        try {
            // 解析特殊結構自然鍵 (相容舊業務資料)
            const orderNoStr = typeof row.orderNo === 'object' ? row.orderNo.value : row.orderNo;

            switch (actionId) {
                case 'ACCEPT_ORDER':
                    await OrderService.acceptOrder(orderNoStr, operatorId);
                    break;
                case 'CANCEL_ORDER':
                    const reason = prompt(`請輸入取消單號 [${orderNoStr}] 的治理原因:`);
                    if (!reason) return;
                    await OrderService.cancelOrder(row.orderId, reason, operatorId);
                    break;
                default:
                    console.warn(`未知的操作意圖: ${actionId}`);
                    return;
            }
            onRefresh();
        } catch (err) {
            setError(`執行操作 [${actionId}] 失敗: ${err.message}`);
        }
    };

    const parsedActions = listSchema.actions.map(actionDef => ({
        label: actionDef.label,
        danger: actionDef.danger,
        showCondition: (row) => {
            if (actionDef.requiredStatuses && !actionDef.requiredStatuses.includes(row.status)) return false;
            if (actionDef.forbiddenStatuses && actionDef.forbiddenStatuses.includes(row.status)) return false;
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