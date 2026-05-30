package com.lims.governance.interceptor;

import com.lims.governance.contract.BehaviorHash;
import com.lims.governance.exception.ContractMismatchException;
import com.lims.governance.exception.GovernanceViolationException;
import com.lims.governance.registry.SchemaRegistry;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * GovernanceInterceptor intercepts all incoming API requests.
 * It enforces the Versioned Runtime Contract by throwing GovernanceViolationException.
 *
 * Constitutional Principle: Failure Taxonomy & Separation of Concerns.
 */
@Provider
@Priority(Priorities.AUTHENTICATION) // Ensure governance is verified before any business logic
public class GovernanceInterceptor implements ContainerRequestFilter {

    private static final String HASH_HEADER = "X-LIMS-Behavior-Hash";
    private static final String INPUT_VERSION_HEADER = "X-LIMS-Input-Version";
    private static final String OUTPUT_VERSION_HEADER = "X-LIMS-Output-Version";
    private static final String FIELD_ID_HEADER = "X-LIMS-Field-ID";

    @Inject
    SchemaRegistry schemaRegistry;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String fieldId = requestContext.getHeaderString(FIELD_ID_HEADER);
        String inputVersion = requestContext.getHeaderString(INPUT_VERSION_HEADER);
        String outputVersion = requestContext.getHeaderString(OUTPUT_VERSION_HEADER);
        String hashValue = requestContext.getHeaderString(HASH_HEADER);

        // [憲法原則：Predictability] Fail-Fast: 強制要求治理標頭
        if (fieldId == null || inputVersion == null || outputVersion == null || hashValue == null) {
            // Using the defined Exception to trigger the Mapper
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