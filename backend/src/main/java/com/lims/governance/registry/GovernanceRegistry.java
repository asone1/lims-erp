package com.lims.governance.registry;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Collections;

/**
 * Constitutional Principle: Predictability by Constitution
 * 此類別為 Immutable，一旦初始化即不可變更。
 */
@ApplicationScoped
public class GovernanceRegistry {
    
    private Map<String, Object> cachedConfig;

    public void initialize(Map<String, Object> config) {
        if (this.cachedConfig != null) {
            throw new IllegalStateException("Governance Plane 嚴禁二次初始化！");
        }
        this.cachedConfig = Collections.unmodifiableMap(config);
    }

    public Object getConfig(String key) {
        return cachedConfig.get(key);
    }
}