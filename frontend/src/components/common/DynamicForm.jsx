// 所屬路徑: src/components/common/DynamicForm.jsx
import React, { useState } from 'react';

/**
 * 共用動態表單元件 (SDUI Engine)
 * 遵守: 絕對禁止硬編碼 - 所有的欄位與佈局由 props.schema 決定
 */
export default function DynamicForm({ schema = [], onSubmit, isLoading }) {
    const [formData, setFormData] = useState({});

    // 動態更新資料的遞迴設值函式 (支援 properties.xxx 這種巢狀結構)
    const handleChange = (targetPath, value) => {
        setFormData(prev => {
            const newData = { ...prev };
            const keys = targetPath.split('.');
            let current = newData;
            
            for (let i = 0; i < keys.length - 1; i++) {
                if (!current[keys[i]]) current[keys[i]] = {};
                current = current[keys[i]];
            }
            current[keys[keys.length - 1]] = value;
            return newData;
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit(formData);
    };

    // 核心渲染引擎
    const renderField = (fieldInfo) => {
        // 💡 治理核心：不要使用 'key' 當解構變數名，改用 fieldPath 或從 fieldInfo.key 提取
        const fieldPath = fieldInfo.key; 
        const { label, type, required } = fieldInfo;
        const options = fieldInfo.options || []; 
        
        // 安全提取巢狀值
        const val = fieldPath.split('.').reduce((o, i) => (o ? o[i] : ''), formData) || '';

        switch (type) {
            case 'select':
                return (
                    <select 
                        value={val} 
                        onChange={(e) => handleChange(fieldPath, e.target.value)} 
                        required={required}
                    >
                        <option value="">請選擇...</option>
                        {options.map(opt => (
                            <option key={opt.value} value={opt.value}>{opt.label}</option>
                        ))}
                    </select>
                );
            case 'text':
            default:
                return (
                    <input 
                        type="text" 
                        value={val} 
                        onChange={(e) => handleChange(fieldPath, e.target.value)} 
                        required={required}
                    />
                );
        }
    };

    return (
        <form onSubmit={handleSubmit} className="dynamic-form">
            {schema.map((field, index) => {
                // 💡 防禦機制：如果 field.key 因為 React 保留字被抽走，降級使用 index 防止噴錯
                const elementKey = field.key || `form-field-${index}`;
                
                return (
                    <div key={elementKey} className="form-group" style={{ marginBottom: '15px' }}>
                        <label style={{ display: 'block', marginBottom: '5px' }}>
                            {field.label} {field.required && <span style={{color: 'red'}}>*</span>}
                        </label>
                        {renderField(field)}
                    </div>
                );
            })}
            <button type="submit" disabled={isLoading} style={{ marginTop: '10px' }}>
                {isLoading ? '處理中...' : '送出'}
            </button>
        </form>
    );
}