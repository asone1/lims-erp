package com.lims.governance;

import com.lims.core.domain.order.Order;
import com.lims.governance.exception.GovernanceErrorCode;
import com.lims.governance.exception.GovernanceViolationException;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constitutional Principle: Capability Rules & Schema Rules.
 * 攔截並審查所有 Domain 變更，確保符合最高治理層校驗。
 */
@ApplicationScoped
public class GovernanceValidator {
    private static final Logger LOG = LoggerFactory.getLogger(GovernanceValidator.class);

    public void check(Order order) {
        // [Capability Rules]: 檢查 Domain 邊界 (例如：訂單狀態是否允許編輯)
        if ("FROZEN".equalsIgnoreCase(order.status())) {
            throw new GovernanceViolationException(
                GovernanceErrorCode.CAPABILITY_FROZEN_DOMAIN.getCode(), 
                GovernanceErrorCode.CAPABILITY_FROZEN_DOMAIN.formatMessage(order.orderId())
            ) {};
        }

        // [Schema Rules]: 規範資料邊界 (例如：檢查必要的屬性是否存在)
        if (order.properties() == null || order.properties().isEmpty()) {
            LOG.warn(GovernanceErrorCode.SCHEMA_MISSING_PROPERTIES.formatMessage(
                order.getClass().getSimpleName() + ":" + order.orderId()
            ));
        }
        
        LOG.info("Governance Plane: Order {} validation passed.", order.orderId());
    }
}