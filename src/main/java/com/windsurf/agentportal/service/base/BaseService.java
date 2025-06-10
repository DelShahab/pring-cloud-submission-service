package com.windsurf.agentportal.service.base;

import java.util.List;
import java.util.Optional;

/**
 * Base service interface that defines common CRUD operations
 * @param <T> Entity type
 * @param <ID> Entity ID type
 */
public interface BaseService<T, ID> {
    
    /**
     * Create a new entity
     * @param entity Entity to create
     * @return Created entity
     */
    T create(T entity);
    
    /**
     * Update an existing entity
     * @param entity Entity to update
     * @return Updated entity
     */
    T update(T entity);
    
    /**
     * Delete an entity by ID
     * @param id Entity ID
     */
    void delete(ID id);
    
    /**
     * Find entity by ID
     * @param id Entity ID
     * @return Optional entity
     */
    Optional<T> findById(ID id);
    
    /**
     * Find all entities
     * @return List of entities
     */
    List<T> findAll();
    
    /**
     * Count entities
     * @return Count
     */
    long count();
}
