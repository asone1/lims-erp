package com.lims.governance.registry;


import com.lims.governance.contract.BehaviorHash;
import com.lims.governance.contract.ContractSignature;
import java.util.Optional;

/**
 * SchemaRegistry interface.
 * Imports BehaviorHash from the contract package.
 */
public interface SchemaRegistry {

    Optional<ContractSignature> getContract(String fieldId, String version);

    boolean isValidBehavior(String fieldId, String inputVersion, String outputVersion, BehaviorHash providedHash);
}