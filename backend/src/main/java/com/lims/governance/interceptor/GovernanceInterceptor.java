package com.lims.governance.interceptor;

// 1. 引入剛剛建立的常數
import static com.lims.governance.contract.ContractConstants.*;

import com.lims.governance.contract.BehaviorHash;
import com.lims.governance.exception.ContractMismatchException;
import com.lims.governance.registry.SchemaRegistry;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class GovernanceInterceptor implements ContainerRequestFilter {

    @Inject
    SchemaRegistry schemaRegistry;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // 2. 直接使用靜態匯入的常數變數
        String fieldId = requestContext.getHeaderString(FIELD_ID_HEADER);
        String inputVersion = requestContext.getHeaderString(INPUT_VERSION_HEADER);
        String outputVersion = requestContext.getHeaderString(OUTPUT_VERSION_HEADER);
        String hashValue = requestContext.getHeaderString(HASH_HEADER);

        // [憲法原則：Predictability] Fail-Fast: 強制要求治理標頭
        if (fieldId == null || inputVersion == null || outputVersion == null || hashValue == null) {
            throw new ContractMismatchException("SYSTEM", "MISSING_HEADERS", "ALL_ZERO_HASH");
        }

        // [憲法原則：Versioned Runtime Contract] 驗證「三位一體」邏輯指紋
        BehaviorHash providedHash = new BehaviorHash(hashValue);
        if (!schemaRegistry.isValidBehavior(fieldId, inputVersion, outputVersion, providedHash)) {
            // [憲法原則：Failure Taxonomy] 契約不符，立即阻斷
            throw new ContractMismatchException(fieldId, inputVersion + "->" + outputVersion);
        }
    }
}