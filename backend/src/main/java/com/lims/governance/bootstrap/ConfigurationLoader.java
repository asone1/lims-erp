package com.lims.governance.bootstrap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Constitutional Principle: Immutable Bootstrap
 * Responsibility: Loading and validating system governance configuration.
 */
@ApplicationScoped
public class ConfigurationLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationLoader.class);

    @Inject
    @ConfigProperty(name = "lims.config.path")
    Optional<String> configPath;

    @Inject
    ObjectMapper objectMapper;

    public JsonNode load() {
        String path = configPath.orElseThrow(() -> 
            new IllegalStateException("LIMS_CONFIG_PATH is not configured. Please set it in application.properties or as an environment variable."));

        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            LOG.error("CRITICAL: Governance configuration file not found at: {}", path);
            throw new RuntimeException("Governance configuration file missing at " + path);
        }

        try {
            LOG.info("Loading governance configuration from path: {}", path);
            String content = Files.readString(filePath);
            return objectMapper.readTree(content);
        } catch (Exception e) {
            throw new RuntimeException("CRITICAL: Governance Config Loading Failed", e);
        }
    }
}