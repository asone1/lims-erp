// 所屬路徑: com.lims.web.schema.SchemaResource
package com.lims.web.schema;

import com.lims.core.application.schema.dto.FormField;
import com.lims.core.application.schema.dto.FormField.Option;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;
import jakarta.inject.Inject;
/**
 * 遵守: 總控層 (Governance Plane) - 集中派發所有的業務表單 Schema
 */
@Path("/api/schemas")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class SchemaResource {

    @Inject
    SchemaConfigLoader configLoader;

    @GET
    @Path("/orders/list")
    public Response getOrderListSchema(@QueryParam("templateType") String templateType) {
        List<FormField> schema = new ArrayList<>();

        // 1. 自動從 Record 映射出來的核心欄位 (此時 required 皆為 true)
        schema.addAll(configLoader.getAllCoreFields());

        // 2. 根據業務動態追加的屬性
        if ("WATER".equalsIgnoreCase(templateType)) {
            schema.add(configLoader.getPropertyField("source")); 
        } else if ("BLOOD".equalsIgnoreCase(templateType)) {
            schema.add(configLoader.getPropertyField("bloodType"));
        }

        return Response.ok(schema).build();
    }
    
    @GET
    @Path("/orders/create")
    public Response getCreateOrderSchema(@QueryParam("templateType") String templateType) {
        
        List<FormField> schema = new ArrayList<>();

        // 1. [基礎合約]: 從 CreateOrderCommand 映射而來的強制屬性
        // 遵守: 這裡 required = true 是因為 Command 建構子有 throws IllegalArgumentException
        schema.add(new FormField("customerId", "客戶代碼", "text", true, null));
        schema.add(new FormField("analystId", "分析師代碼", "text", true, null));

        // 2. [動態擴展合約]: 對應 Map<String, Object> properties
        // 根據前端傳來的 templateType (業務類型) 動態決定要填寫哪些 properties
        if ("WATER".equalsIgnoreCase(templateType)) {
            schema.addAll(getWaterTestProperties());
        } else {
            schema.addAll(getBloodTestProperties()); // 預設或指定為血液
        }

        return Response.ok(schema).build();
    }

    private List<FormField> getBloodTestProperties() {
        return List.of(
            new FormField("properties.bloodType", "血型", "select", true, List.of(
                new Option("A型", "A"),
                new Option("B型", "B"),
                new Option("O型", "O"),
                new Option("AB型", "AB")
            )),
            new FormField("properties.fastingHours", "空腹小時數", "number", true, null)
        );
    }

    private List<FormField> getWaterTestProperties() {
        return List.of(
            new FormField("properties.source", "水源地", "text", true, null),
            new FormField("properties.phValue", "預估酸鹼值 (pH)", "number", false, null) // 非必填
        );
    }

    
}