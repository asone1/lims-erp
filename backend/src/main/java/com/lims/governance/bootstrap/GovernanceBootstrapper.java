package com.lims.governance.bootstrap;

import com.lims.governance.registry.GovernanceRegistry;

import io.quarkus.runtime.StartupEvent;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class GovernanceBootstrapper {

    @Inject GovernanceRegistry registry;
    
    @Inject ConfigurationLoader loader;

    void onStart(@Observes StartupEvent ev) {
        // 1. 啟動時自動載入治理配置
        JsonNode config = loader.load();

        // 2. 初始化註冊表 (模擬邏輯)
        // registry.initialize(config);
    }
}