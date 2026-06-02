// 所屬路徑: src/App.jsx
import React, { useState, useEffect } from 'react';
//import { OrderService } from './src/components/domain/order/OrderService';
import { OrderService } from './components/domain/order/OrderService';
//import { OrderService } from '@domain/order/OrderService';
import OrderForm from '@domain/order/OrderForm';
import OrderList from '@domain/order/OrderList';


function App() {
    const [orders, setOrders] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    // 遵守: 全域副作用控制，統一由容器層調度資料
    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        setIsLoading(true);
        setError(null);
        try {
            const data = await OrderService.getAllOrders();
            setOrders(Array.isArray(data) ? data : []);
        } catch (err) {
            setError('載入 LIMS 訂單時發生非預期異常，請聯絡系統管理員。');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="container">
            <h1>LIMS-ERP 訂單管理系統</h1>
            
            {error && (
                <div className="error-message" style={{ color: 'red', border: '1px solid red', padding: '10px' }}>
                    {error}
                </div>
            )}

            {/* 遵守: 元件職責分離 - 表單建立元件 */}
            <OrderForm onOrderCreated={fetchOrders} setIsLoading={setIsLoading} setError={setError} />
            
            <hr />

            {/* 遵守: 元件職責分離 - 清單展示與狀態機推進元件 */}
            <OrderList orders={orders} isLoading={isLoading} onRefresh={fetchOrders} setError={setError} />
        </div>
    );
}

export default App;