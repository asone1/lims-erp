package com.lims.shared.domain.repository;

import java.util.Optional;

/**
 * Contract for read operations.
 */
public interface ReadOnlyRepository<T, ID> extends Repository<T, ID> {
    Optional<T> findById(ID id);
}