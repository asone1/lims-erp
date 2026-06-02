package com.lims.governance.contract;

/**
 * 治理層契約常數定義中心
 * 遵守: 絕對禁止硬編碼 (No Hardcoding) - 集中管理系統層級 Header 契約
 */
public final class ContractConstants {

    private ContractConstants() {
        // 防止被實例化
    }

    public static final String HASH_HEADER = "X-LIMS-Behavior-Hash";
    public static final String INPUT_VERSION_HEADER = "X-LIMS-Input-Version";
    public static final String OUTPUT_VERSION_HEADER = "X-LIMS-Output-Version";
    public static final String FIELD_ID_HEADER = "X-LIMS-Field-ID";
    
    // 如果未來有操作員追蹤，也可以放這
    public static final String OPERATOR_ID_HEADER = "X-Operator-Id";
}