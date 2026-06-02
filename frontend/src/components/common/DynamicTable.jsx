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
    
    // 輔助函式：解析巢狀屬性 (例如 'orderNo.value' 或 'properties.bloodType')
    const getNestedValue = (obj, path) => {
        if (!path) return undefined;
        return path.split('.').reduce((acc, part) => acc && acc[part], obj);
    };

    if (isLoading) return <div style={{ padding: '20px' }}>載入中，請稍候...</div>;
    if (!data || data.length === 0) return <div style={{ padding: '20px' }}>目前尚無任何資料。</div>;

    return (
        <table className="dynamic-table" style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
            <thead>
                <tr style={{ backgroundColor: '#f5f5f5', borderBottom: '2px solid #ccc' }}>
                    {columns.map((col, index) => {
                        // 💡 治理核心：React 的 key 屬性由 col.dataIndex 或 index 提供，但真正顯示名稱點出 col.label
                        const headerKey = col.dataIndex || `col-${index}`;
                        return (
                            <th key={headerKey} style={{ padding: '10px', textAlign: 'left' }}>
                                {col.label || headerKey}
                            </th>
                        );
                    })}
                    {rowActions.length > 0 && (
                        <th style={{ padding: '10px', textAlign: 'left' }}>操作 (Actions)</th>
                    )}
                </tr>
            </thead>
            <tbody>
                {data.map((row, rIndex) => (
                    <tr key={row.orderId || row.id || rIndex} style={{ borderBottom: '1px solid #eee' }}>
                        
                        {/* 1. 動態渲染資料欄位 */}
                        {columns.map((col, cIndex) => {
                            // 💡 治理核心：改用 col.dataIndex 來安全地獲取後端欄位路徑
                            const fieldPath = col.dataIndex;
                            let cellValue = getNestedValue(row, fieldPath);
                            
                            // 若有自訂的渲染邏輯，則呼叫 customRender
                            if (col.customRender) {
                                cellValue = col.customRender(cellValue, row);
                            }
                            
                            // 遇到物件無法直接渲染，轉為字串保護畫面不崩潰
                            if (typeof cellValue === 'object' && cellValue !== null) {
                                cellValue = JSON.stringify(cellValue);
                            }
                            
                            const cellKey = fieldPath || `cell-${cIndex}`;
                            return (
                                <td key={cellKey} style={{ padding: '10px' }}>
                                    {col.dataIndex === 'status' ? (
                                        <span className={`badge-${cellValue}`} style={{
                                            padding: '4px 8px', 
                                            borderRadius: '4px',
                                            background: cellValue === 'ACCEPTED' ? '#e6f7ff' : '#fff0f6',
                                            color: cellValue === 'ACCEPTED' ? '#1890ff' : '#eb2f96'
                                        }}>
                                            {cellValue || '-'}
                                        </span>
                                    ) : (
                                        cellValue || '-'
                                    )}
                                </td>
                            );
                        })}

                        {/* 2. 動態渲染操作按鈕 */}
                        {rowActions.length > 0 && (
                            <td style={{ padding: '10px' }}>
                                {rowActions.map((action, aIndex) => {
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