package com.lims.governance.registry;

import com.lims.governance.contract.BehaviorHash;
import com.lims.governance.contract.ContractSignature;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

/**
 * Default in-memory implementation of SchemaRegistry.
 * Marked as @ApplicationScoped so Quarkus can inject it.
 */
@ApplicationScoped
public class InMemorySchemaRegistry implements SchemaRegistry {

    @Override
    public Optional<ContractSignature> getContract(String fieldId, String version) {
        return Optional.empty();
    }

    @Override
    public boolean isValidBehavior(String fieldId, String inputVersion, String outputVersion, BehaviorHash providedHash) {
        return true; // Defaulting to true for development/simulation
    }
}