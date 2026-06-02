// 所屬路徑: src/components/domain/order/OrderForm.jsx
import React, { useState, useEffect } from 'react';
import { OrderService } from '@domain/order/OrderService';
import DynamicForm from '@common/DynamicForm';
import { getFieldUiConfig } from '@config/ui-label-config'; 

export default function OrderForm({ onOrderCreated, setError, templateType = "WATER" }) {
    const [isLoading, setIsLoading] = useState(false);
    const [orderSchema, setOrderSchema] = useState([]);

    useEffect(() => {
        const fetchFormSchema = async () => {
            try {
                const rawSchemaArray = await OrderService.getSchema(`orders/create?templateType=${templateType}`);
                
                console.log("OrderForm 接收到的後端原始 Schema:", rawSchemaArray);
                
                const decoratedSchema = rawSchemaArray.map(field => {
                    const actualKey = field.key || field.fieldName;
                    
                    if (!actualKey) return null;
                
                    // 1. 去前端配置檔查表
                    const targetClass = actualKey.startsWith('properties.') ? "SAMPLE" : "ORDER";
                    const uiConfig = getFieldUiConfig(targetClass, actualKey);
                
                    // 2. 絕對權重決策鏈：
                    let finalLabel = "";
                
                    // 狀況 A：如果前端查出來的 label 不是英文 Key，代表前端有在配置檔「特意覆寫」它（例如 "訂單客戶"）
                    if (uiConfig && uiConfig.label && uiConfig.label !== actualKey) {
                        finalLabel = uiConfig.label;
                    } 
                    // 狀況 B：如果前端沒特意覆寫（或者是回傳了英文原名），則 100% 優先採用後端送來的優美中文（例如 "客戶代碼"、"水源地"）
                    else if (field.label) {
                        finalLabel = field.label;
                    } 
                    // 狀況 C：如果前後端都沒有定義中文，最後底線才是用英文 Key
                    else {
                        finalLabel = actualKey;
                    }
                
                    return {
                        key: actualKey,
                        label: finalLabel, // 這樣一來，customerId 就會吃到前端的 "訂單客戶"；properties.source 就會吃到後端的 "水源地"！
                        type: field.type || "text",
                        required: field.required !== undefined ? field.required : true,
                        options: uiConfig.options || [] // 下拉選單選項依然保留前端注入
                    };
                }).filter(Boolean);

                console.log("OrderForm 最終組裝完成的裝飾 Schema:", decoratedSchema);
                setOrderSchema(decoratedSchema);

            } catch (err) {
                console.error("OrderForm 載入失敗詳細原因:", err);
                setError('無法載入表單欄位設定，請確認後端服務狀態。');
            }
        };
        fetchFormSchema();
    }, [setError, templateType]);

    const handleCommandSubmit = async (formData) => {
        try {
            setIsLoading(true);
            await OrderService.createOrder(formData);
            alert('訂單建立成功！');
            onOrderCreated();
        } catch (err) {
            setError(err.message || '建立訂單失敗');
        } finally {
            setIsLoading(false);
        }
    };

    if (orderSchema.length === 0) return <p>正在載入表單結構...</p>;

    return (
        <section className="order-form-section">
            <h2>建立新訂單 (SDUI 後端驅動)</h2>
            <DynamicForm 
                schema={orderSchema} 
                onSubmit={handleCommandSubmit} 
                isLoading={isLoading} 
            />
        </section>
    );
}