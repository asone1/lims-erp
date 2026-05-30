package com.lims.core.application.order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.lims.governance.GovernanceValidator;
import com.lims.shared.domain.exception.LimsBaseException;
import com.lims.shared.event.EventBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lims.core.domain.order.event.OrderCreatedEvent;
import com.lims.core.application.order.dto.CreateOrderCommand;
// import com.lims.governance.contract.SchemaMismatchException;
import com.lims.core.domain.order.Order;
import com.lims.core.domain.order.OrderFactory;
import com.lims.core.domain.order.OrderId;
import com.lims.core.domain.order.repository.OrderQuery;
import com.lims.core.domain.order.repository.OrderCommand;
import com.lims.core.domain.order.repository.OrderCommandRepository;
import com.lims.core.domain.order.repository.OrderQueryRepository;
import com.lims.core.infrastructure.id.QuarkusGcpIdDispatcher;
import java.util.Map;
import jakarta.transaction.Transactional;
import java.util.UUID;
import java.util.List;
import org.slf4j.MDC;
import java.util.Optional;

@ApplicationScoped
public class OrderService {
    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);

   @Inject @OrderCommand OrderCommandRepository repository;
    @Inject @OrderQuery OrderQueryRepository queryRepository;
    @Inject GovernanceValidator validator;
    @Inject EventBus eventBus;

    @Inject
    QuarkusGcpIdDispatcher idDispatcher;

    @Inject
    OrderFactory orderFactory;

    
    public Optional<Order> findById(UUID orderId) {
        return queryRepository.findById(orderId);
    }

    public Optional<Order> findByOrderNo(String rawOrderNo) {
        return queryRepository.findByOrderNo(OrderId.of(rawOrderNo));
    }
    
    public List<Order> findAll() {
        // 查詢操作通常不需要 Transactional，除非涉及延遲載入 (Lazy Loading) 的處理
        return queryRepository.findAll();
    }

    /**
     * 遵守: 預期失敗與效能防禦 - 強制分頁，阻絕 OOM 風險
     */
    // public List<Order> findOrders(int pageIndex, int pageSize) {
    //     return queryRepository.findOrders(pageIndex, pageSize);
    // }


    @Transactional
    public Order createOrder(CreateOrderCommand command) { // 參數改為接收 Command
        // 1. [基礎設施] 獲取中央分派業務 ID
        String rawId = idDispatcher.dispatch(OrderId.class);
OrderId orderId = OrderId.of(rawId);

        // 將訂單號加入 MDC，確保後續 Log 都有 Trace 追蹤
        MDC.put("orderNo",orderId.getValue());

        try {
            // 2. [領域工廠] 結合 Command 的資料與系統生成的 ID，創建聚合根
            Order order = orderFactory.createInitialOrder(
                        // 將訂單號加入 MDC，確保後續 Log 都有 Trace 追蹤
                orderId,
                command.customerId(),
                command.analystId(),
                command.properties()
            );

            // 3. [憲法原則：Governance Plane] 檢查業務規則 (例如：公司權限、狀態限制)
            validator.check(order);

            // 4. [基礎設施] 持久化
            repository.save(order);

            // 5. [憲法原則：Event-Driven] 發布 Business Event
            // 絕對禁止直接呼叫外部服務，必須透過 Event Bus
            eventBus.publish(new OrderCreatedEvent(order));

            return order; // 返回給 API 層

        } finally {
            MDC.remove("orderNo"); // 清理 MDC 避免 ThreadLocal 污染
        }
    
    }



    @Transactional
    public void acceptOrder(String rawOrderNo, String operatorId) {
        
        // 1. 轉為強型別業務 ID
        OrderId orderNo = OrderId.of(rawOrderNo);
        
        // 2. 透過 Repository 以【業務 ID】查找聚合根 (而不是 UUID)
        Order order = queryRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new LimsBaseException(
                    "ERR-ORD-404", "找不到指定的訂單編號: " + rawOrderNo
                ));
        
        // 3. 呼叫聚合根內部的狀態機 (領域邏輯)
        Order acceptedOrder = order.accept(operatorId);
        
        // 4. 領域規則防護網 (Governance)
        validator.check(acceptedOrder);
        
        // 5. 持久化變更後的聚合根
        repository.save(acceptedOrder);
        
        // 遵守: 關鍵狀態變更必須留存日誌
        LOG.info("訂單 [{}] 已由操作員 [{}] 成功接受", rawOrderNo, operatorId);
    }

    @Transactional
    public void deleteOrder(UUID orderId, String reason, String operatorId) {
        Order order = queryRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        
        Order cancelledOrder = order.cancel(reason, operatorId);
        validator.check(cancelledOrder); // 憲法原則：檢查目前狀態是否允許取消
        repository.save(cancelledOrder);
    }
}