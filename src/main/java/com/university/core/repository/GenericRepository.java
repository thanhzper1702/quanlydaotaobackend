package com.university.core.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID extends Serializable> {
    Optional<T> findById(ID id);
    List<T> findAll();
    T save(T entity);
    T update(T entity);
    void delete(T entity);
    void deleteById(ID id);
    long count();
}
