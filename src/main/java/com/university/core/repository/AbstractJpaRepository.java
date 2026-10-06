package com.university.core.repository;

import com.university.core.config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractJpaRepository<T, ID extends Serializable> implements GenericRepository<T, ID> {

    protected final Class<T> entityClass;

    protected AbstractJpaRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected <R> R execute(Function<EntityManager, R> action) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return action.apply(em);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    protected void executeInTransaction(Consumer<EntityManager> action) {
        executeInTransaction(em -> {
            action.accept(em);
            return null;
        });
    }

    protected <R> R executeInTransaction(Function<EntityManager, R> action) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = action.apply(em);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Transaction error in repository: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        return execute(em -> Optional.ofNullable(em.find(entityClass, id)));
    }

    @Override
    public List<T> findAll() {
        return execute(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(root);
            TypedQuery<T> query = em.createQuery(cq);
            return query.getResultList();
        });
    }

    @Override
    public T save(T entity) {
        executeInTransaction(em -> {
            em.persist(entity);
        });
        return entity;
    }

    @Override
    public T update(T entity) {
        return executeInTransaction(em -> {
            return em.merge(entity);
        });
    }

    @Override
    public void delete(T entity) {
        executeInTransaction(em -> {
            T managed = em.contains(entity) ? entity : em.merge(entity);
            em.remove(managed);
        });
    }

    @Override
    public void deleteById(ID id) {
        executeInTransaction(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
        });
    }

    @Override
    public long count() {
        return execute(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            cq.select(cb.count(cq.from(entityClass)));
            return em.createQuery(cq).getSingleResult();
        });
    }
}
