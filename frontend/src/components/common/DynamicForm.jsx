// 所屬路徑: src/components/common/DynamicForm.jsx
import React, { useState } from 'react';

/**
 * 共用動態表單元件 (SDUI Engine)
 * 遵守: 絕對禁止硬編碼 - 所有的欄位與佈局由 props.schema 決定
 */
export default function DynamicForm({ schema = [], onSubmit, isLoading }) {
    const [formData, setFormData] = useState({});

    // 動態更新資料的遞迴設值函式 (支援 properties.xxx 這種巢狀結構)
    const handleChange = (key, value) => {
        setFormData(prev => {
            const newData = { ...prev };
            const keys = key.split('.');
            let current = newData;
            
            // 處理巢狀屬性，例如 'properties.sampleType'
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
        // 遵守: 將資料組裝完成後，往上拋給調用端 (Command Sender)
        onSubmit(formData);
    };

    // 核心渲染引擎
    const renderField = (fieldInfo) => {
        const { key, label, type, options, required } = fieldInfo;
        // 提取當前值 (支援提取巢狀值)
        const val = key.split('.').reduce((o, i) => (o ? o[i] : ''), formData) || '';

        switch (type) {
            case 'select':
                return (
                    <select 
                        value={val} 
                        onChange={(e) => handleChange(key, e.target.value)} 
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
                        onChange={(e) => handleChange(key, e.target.value)} 
                        required={required}
                    />
                );
        }
    };

    return (
        <form onSubmit={handleSubmit} className="dynamic-form">
            {schema.map((field) => (
                <div key={field.key} className="form-group" style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', marginBottom: '5px' }}>
                        {field.label} {field.required && <span style={{color: 'red'}}>*</span>}
                    </label>
                    {renderField(field)}
                </div>
            ))}
            <button type="submit" disabled={isLoading} style={{ marginTop: '10px' }}>
                {isLoading ? '處理中...' : '送出'}
            </button>
        </form>
    );
}