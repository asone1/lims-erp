// 所屬路徑: src/components/common/DynamicTable.jsx
import React from 'react';

/**
 * 共用動態資料表 (SDUI List Engine)
 * 遵守: 絕對禁止硬編碼 - 欄位呈現與按鈕邏輯皆由傳入的 Schema 決定
 */
export default function DynamicTable({ 
    columns = [], 
    data = [], 
    rowActions = [], 
    isLoading 
}) {
    
    // 輔助函式：解析巢狀屬性 (例如 'orderNo.value' 或 'properties.sampleType')
    const getNestedValue = (obj, path) => {
        return path.split('.').reduce((acc, part) => acc && acc[part], obj);
    };

    if (isLoading) return <div style={{ padding: '20px' }}>載入中，請稍候...</div>;
    if (!data || data.length === 0) return <div style={{ padding: '20px' }}>目前尚無任何資料。</div>;

    return (
        <table className="dynamic-table" style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
            <thead>
                <tr style={{ backgroundColor: '#f5f5f5', borderBottom: '2px solid #ccc' }}>
                    {columns.map(col => (
                        <th key={col.key} style={{ padding: '10px', textAlign: 'left' }}>
                            {col.label}
                        </th>
                    ))}
                    {rowActions.length > 0 && (
                        <th style={{ padding: '10px', textAlign: 'left' }}>操作 (Actions)</th>
                    )}
                </tr>
            </thead>
            <tbody>
                {data.map((row, index) => (
                    // 實務上應優先使用 row.id，若無則降級使用 index
                    <tr key={row.orderId || row.id || index} style={{ borderBottom: '1px solid #eee' }}>
                        
                        {/* 1. 動態渲染資料欄位 */}
                        {columns.map(col => {
                            let cellValue = getNestedValue(row, col.key);
                            // 若有自訂的渲染邏輯 (如日期格式化)，則呼叫 customRender
                            if (col.customRender) {
                                cellValue = col.customRender(cellValue, row);
                            }
                            // 遇到物件無法直接渲染，轉為字串保護畫面不崩潰
                            if (typeof cellValue === 'object' && cellValue !== null) {
                                cellValue = JSON.stringify(cellValue);
                            }
                            return (
                                <td key={col.key} style={{ padding: '10px' }}>
                                    {cellValue || '-'}
                                </td>
                            );
                        })}

                        {/* 2. 動態渲染操作按鈕 (結合狀態機檢驗) */}
                        {rowActions.length > 0 && (
                            <td style={{ padding: '10px' }}>
                                {rowActions.map((action, aIndex) => {
                                    // 遵守: 領域狀態機防護 - 檢查該按鈕在此狀態下是否允許顯示
                                    const canShow = action.showCondition ? action.showCondition(row) : true;
                                    if (!canShow) return null;

                                    return (
                                        <button 
                                            key={aIndex}
                                            onClick={() => action.onClick(row)}
                                            style={{ 
                                                marginRight: '5px', 
                                                color: action.danger ? 'red' : 'inherit' 
                                            }}
                                        >
                                            {action.label}
                                        </button>
                                    );
                                })}
                            </td>
                        )}
                    </tr>
                ))}
            </tbody>
        </table>
    );
}