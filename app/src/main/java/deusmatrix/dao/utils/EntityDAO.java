package deusmatrix.dao.utils;

import deusmatrix.utils.Logger;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

public abstract class EntityDAO<T> {
    protected final EntityManagerFactory entityManagerFactory;

    public EntityDAO(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public T get(Long id) {
        if (!queryIsReadyToCreate(id)) {
            return null;
        }

        return executeSingleResultQuery(manager -> {
            CriteriaBuilder builder = manager.getCriteriaBuilder();
            CriteriaQuery<T> query = getSelectQuery(builder, id);
            TypedQuery<T> preparedQuery = manager.createQuery(query);
            return preparedQuery.getSingleResult();
        });
    }

    public List<T> getAll() {
        if (!queryIsReadyToCreate()) {
            return Collections.emptyList();
        }

        List<T> result = executeListResultQuery(manager -> {
            CriteriaBuilder builder = manager.getCriteriaBuilder();
            CriteriaQuery<T> query = getSelectAllQuery(builder);
            TypedQuery<T> preparedQuery = manager.createQuery(query);
            return preparedQuery.getResultList();
        });

        return result == null ? Collections.emptyList() : result;
    }

    public boolean update(T entity) {
        if (!queryIsReadyToCreate(entity)) {
            return false;
        }

        return executeQuery(manager -> manager.merge(entity));
    }

    public boolean remove(T entity) {
        if (!queryIsReadyToCreate(entity)) {
            return false;
        }

        AtomicBoolean removed = new AtomicBoolean(false);
        boolean executed = executeQuery(manager -> {
            T foundEntity = searchEntity(entity, manager);
            if (foundEntity != null) {
                manager.remove(foundEntity);
                removed.set(true);
            }
        });

        return executed && removed.get();
    }

    public boolean create(T entity) {
        if (!queryIsReadyToCreate(entity)) {
            return false;
        }

        AtomicBoolean created = new AtomicBoolean(false);
        boolean executed = executeQuery(manager -> {
            T foundEntity = searchEntity(entity, manager);
            if (foundEntity == null) {
                manager.persist(entity);
                created.set(true);
            } else {
                Logger.getInstance().warning("Can't create entity: it already exists");
            }
        });

        return executed && created.get();
    }

    protected boolean executeQuery(QueryBody queryBody) {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();
            queryBody.execute(manager);
            transaction.commit();
            return true;
        } catch (PersistenceException | IllegalArgumentException e) {
            rollback(transaction);
            Logger.getInstance().warning("Can't execute query: " + e.getMessage());
            return false;
        } finally {
            close(manager);
        }
    }

    protected <M> M executeSingleResultQuery(SingleResultQueryBody<M> queryBody) {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();
            M result = queryBody.execute(manager);
            transaction.commit();
            return result;
        } catch (PersistenceException | IllegalArgumentException e) {
            rollback(transaction);
            Logger.getInstance().warning("Can't execute single result query: " + e.getMessage());
            return null;
        } finally {
            close(manager);
        }
    }

    protected <M> List<M> executeListResultQuery(ListResultQueryBody<M> queryBody) {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();
            List<M> result = queryBody.execute(manager);
            transaction.commit();
            return result;
        } catch (PersistenceException | IllegalArgumentException e) {
            rollback(transaction);
            Logger.getInstance().warning("Can't execute list result query: " + e.getMessage());
            return Collections.emptyList();
        } finally {
            close(manager);
        }
    }

    protected abstract CriteriaQuery<T> getSelectQuery(CriteriaBuilder builder, Long id);

    protected abstract CriteriaQuery<T> getSelectAllQuery(CriteriaBuilder builder);

    protected abstract T searchEntity(T entity, EntityManager manager);

    protected boolean queryIsReadyToCreate(T entity) {
        if (entity == null) {
            Logger.getInstance().warning("Empty object");
            return false;
        }
        return queryIsReadyToCreate();
    }

    protected boolean queryIsReadyToCreate(Long id) {
        if (id == null) {
            Logger.getInstance().warning("Empty identifier");
            return false;
        }
        return queryIsReadyToCreate();
    }

    protected boolean queryIsReadyToCreate() {
        if (entityManagerFactory == null) {
            Logger.getInstance().warning("Empty entity manager factory");
            return false;
        }
        return true;
    }

    private void rollback(EntityTransaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }

    private void close(EntityManager manager) {
        if (manager != null && manager.isOpen()) {
            manager.close();
        }
    }
}
