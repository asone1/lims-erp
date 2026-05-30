package com.lims.core.infrastructure.bootstrap;

import com.lims.core.application.order.OrderService;
import com.lims.core.domain.order.Order;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import com.lims.core.application.order.dto.CreateOrderCommand;
import java.util.HashMap;

@ApplicationScoped
public class OrderBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(OrderBootstrap.class);

    @Inject
    OrderService orderService;

    /**
     * 模擬 Main 進入點：當應用程式啟動時觸發
     */
    void onStart(@Observes StartupEvent ev) {
        LOG.info("Bootstrapping LIMS order initialization simulation.");

        Order mockOrder = orderService.createOrder(new CreateOrderCommand("001","001",new HashMap<String, Object>())) ;

        LOG.info("Creating mock order. State: {}", mockOrder);
       
    }
}