package com.lims.shared.domain;

import java.util.Map;

/**
 * Interface to mark domain objects as exportable.
 * Constitutional Principle: Schema-Driven I/O.
 * The exporter uses reflection and SchemaRegistry metadata to build the layout.
 */
public interface Exportable {
    
    /**
     * Returns a map of fieldId to value, using SchemaRegistry keys.
     */
    Map<String, Object> toExportMap();

    /**
     * Unique identifier for the ExportTemplate in the Registry.
     */
    String getTemplateId();
}