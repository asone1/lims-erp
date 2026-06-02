package com.lims.web.schema;

import com.lims.core.application.schema.dto.FormField;
import com.lims.core.domain.order.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import io.quarkus.runtime.StartupEvent;
import java.lang.reflect.RecordComponent;
import java.time.Instant;
import java.util.*;

@ApplicationScoped
public class SchemaConfigLoader {

    private final Map<String, FormField> coreFields = new LinkedHashMap<>();
    private final Map<String, FormField> propertyFields = new LinkedHashMap<>();

    void onStart(@Observes StartupEvent ev) {
        try {
            // 1. 動態解析 Order Record 的核心欄位
            for (RecordComponent comp : Order.class.getRecordComponents()) {
                String name = comp.getName();
                Class<?> type = comp.getType();
                
                // 跳過我們不想直接暴露在列表基礎欄位的特殊欄位 (例如動態 properties 本身)
                if ("properties".equals(name)) {
                    continue;
                }

                // 根據 Java 型態映射到前端 UI 類型
                String uiType = determineUiType(type);
                
                // 需求：required 預設都是 true
                coreFields.put(name, new FormField(name, null, uiType, true, null));
            }

            // 2. 模擬動態擴充屬性 (因為 properties 在 Record 中是 Map，反射抓不到 Key，需在此手動註冊合約)
            // 需求：required 預設都是 true
            propertyFields.put("bloodType", new FormField("properties.bloodType", null, "select", true, null));
            propertyFields.put("fastingHours", new FormField("properties.fastingHours", null, "number", true, null));
            propertyFields.put("source", new FormField("properties.source", null, "text", true, null));
            propertyFields.put("phValue", new FormField("properties.phValue", null, "number", true, null));

        } catch (Exception e) {
            throw new RuntimeException("Governance Plane Failure: Failed to auto-generate schema from Record", e);
        }
    }

    /**
     * 將 Java 型態對應到前端 UI 元件型態
     */
    private String determineUiType(Class<?> type) {
        if (type == UUID.class || type == String.class || type.getSimpleName().equals("OrderId")) {
            return "text";
        } else if (type == Instant.class) {
            return "datetime";
        }
        return "text"; // 預設型態
    }

    public FormField getCoreField(String key) {
        return coreFields.get(key);
    }

    public FormField getPropertyField(String key) {
        return propertyFields.get(key);
    }

    public List<FormField> getAllCoreFields() {
        return new ArrayList<>(coreFields.values());
    }
}