package com.windsurf.agentportal.service.base;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base service implementation that provides common CRUD operations
 * @param <T> Entity type
 * @param <ID> Entity ID type
 * @param <R> Repository type
 */
@RequiredArgsConstructor
public abstract class BaseServiceImpl<T, ID, R extends CrudRepository<T, ID>> implements BaseService<T, ID> {
    
    protected final R repository;
    
    @Override
    public T create(T entity) {
        return repository.save(entity);
    }
    
    @Override
    public T update(T entity) {
        return repository.save(entity);
    }
    
    @Override
    public void delete(ID id) {
        repository.deleteById(id);
    }
    
    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }
    
    @Override
    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        repository.findAll().forEach(entities::add);
        return entities;
    }
    
    @Override
    public long count() {
        return repository.count();
    }
}
