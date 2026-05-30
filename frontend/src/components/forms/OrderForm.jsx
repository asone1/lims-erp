// 所屬路徑: src/components/OrderForm.jsx
import React, { useState, useEffect } from 'react';
import { OrderService } from '../services/OrderService';
import DynamicForm from './common/DynamicForm';

export default function OrderForm({ onOrderCreated, setError }) {
    const [isLoading, setIsLoading] = useState(false);
    const [orderSchema, setOrderSchema] = useState([]);

    useEffect(() => {
        // 遵守: 動態掛載 - 畫面載入時向後端請求 Form Schema (可帶入業務類型)
        const fetchFormSchema = async () => {
            try {
                // 實務上 templateType 可由下拉選單切換，這裡預設抓 WATER 類型的 Schema
                const schema = await OrderService.getSchema('orders/create?templateType=WATER');
                setOrderSchema(schema);
            } catch (err) {
                setError('無法載入表單欄位設定，請確認後端服務狀態。');
            }
        };
        fetchFormSchema();
    }, [setError]);

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