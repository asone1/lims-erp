package com.lims.governance.contract;

/**
 * Single Source of Truth for all Contract Versions.
 * Constitutional Principle: Versioned Runtime Contract.
 */
public final class ContractRegistry {

    private ContractRegistry() {} // Prevent instantiation

    public static final class LimsResultEvents {
        public static final String CREATED_V1 = "1.0.0";
        public static final String RECEIVED_V1 = "1.0.0";
        public static final String VALIDATED_V1 = "1.0.0";
    }

    // Future-proofing: easily add new modules here
    public static final class ERPConnector {
        public static final String SYNC_V1 = "1.0.0";
    }
}