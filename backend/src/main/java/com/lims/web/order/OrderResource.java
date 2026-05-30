// 所屬路徑: com.lims.web.order.OrderResource
package com.lims.web.order; 

import com.lims.core.application.order.OrderService; 
import com.lims.core.application.order.dto.CreateOrderCommand;
import com.lims.core.application.order.dto.CancelOrderRequest; // 遵守: DTO 封裝請求體
import com.lims.core.domain.order.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.UUID;

/**
 * 遵守: Resource 層僅負責 HTTP 協議轉換與路由，不含任何業務邏輯
 */
@Path("/api/orders") 
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrderResource.class);

    @Inject
    OrderService orderService;

    @GET
    public Response getAllOrders() {
        LOG.info("Received request to fetch all orders.");
        List<Order> orders = orderService.findAll();
        // 遵守: 架構建議 - 實務上應轉為 OrderResponseDTO，此處為求展示簡化暫不展開
        return Response.ok(orders).build();
    }

    @POST
    public Response createOrder(CreateOrderCommand command) {
        // 遵守: CQRS 模式，由 Command 承載意圖，拒絕直接映射 Aggregate
        LOG.info("Received request to create order for customer: {}", command.customerId());
        Order createdOrder = orderService.createOrder(command);
        return Response.status(Response.Status.CREATED)
                       .entity(createdOrder) 
                       .build();
    }

    @PATCH // 修正: 推進狀態機在 REST 語意中，使用 PATCH 優於 PUT
    @Path("/{orderNo}/accept")  // 遵守: 路由使用業務自然鍵 (orderNo)
    public Response acceptOrder(
            @PathParam("orderNo") String orderNo, 
            @HeaderParam("X-Operator-Id") String operatorId) { 

        LOG.info("Received request to accept order [{}], Operator: {}", orderNo, operatorId);
        
        // 1. Web 層不進行業務判斷，直接委派給 Application 層
        orderService.acceptOrder(orderNo, operatorId);

        // 2. 處理成功，回傳標準 HTTP 200 OK
        return Response.ok().build();
    }

    /**
     * 取消訂單 (邏輯刪除)
     * 遵守: 預期失敗與安全審計 - DELETE 必須包含主體 (原因) 與追蹤者 (操作員)
     */
    @DELETE
    @Path("/{orderId}") // 遵守: 基礎設施與生命週期終點操作使用系統代理鍵 (UUID)
    public Response cancelOrder(
            @PathParam("orderId") UUID orderId,
            @HeaderParam("X-Operator-Id") String operatorId,
            CancelOrderRequest request) { // 遵守: 依賴 DTO 接收取消原因，拒絕硬編碼

        LOG.info("Received request to cancel order UUID [{}], Operator: {}, Reason: {}", 
                orderId, operatorId, request.reason());

        // 委派 Application Service 執行狀態轉換與治理驗證
        orderService.deleteOrder(orderId, request.reason(), operatorId);

        return Response.noContent().build(); // HTTP 204 No Content 是 DELETE 成功的標準回應
    }
}