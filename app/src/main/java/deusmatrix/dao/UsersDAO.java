package deusmatrix.dao;

import deusmatrix.dao.utils.EntityDAO;
import deusmatrix.models.Statistic;
import deusmatrix.models.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class UsersDAO extends EntityDAO<User> {
    public UsersDAO(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    private CriteriaQuery<Statistic> getUserStatisticCharacteristicsQuery(CriteriaBuilder builder, User user) {
        CriteriaQuery<Statistic> preparedQuery = null;

        preparedQuery = builder.createQuery(Statistic.class);
        Root<User> root = preparedQuery.from(User.class);
        preparedQuery = preparedQuery.select(root.join("statistic"));
        preparedQuery = preparedQuery.where(builder.equal(root.get("id"), user.getId()));

        return preparedQuery;
    }

    @Override
    protected CriteriaQuery<User> getSelectQuery(CriteriaBuilder builder, Long id) {
        CriteriaQuery<User> preparedQuery = null;

        preparedQuery = builder.createQuery(User.class);
        Root<User> root = preparedQuery.from(User.class);
        preparedQuery = preparedQuery.select(root);
        preparedQuery = preparedQuery.where(builder.equal(root.get("id"), id));

        return preparedQuery;
    }

    @Override
    protected CriteriaQuery<User> getSelectAllQuery(CriteriaBuilder builder) {
        CriteriaQuery<User> preparedQuery = null;

        preparedQuery = builder.createQuery(User.class);
        Root<User> root = preparedQuery.from(User.class);
        preparedQuery = preparedQuery.select(root);

        return preparedQuery;
    }

    @Override
    protected User searchEntity(User entity, EntityManager manager) {
        return entity == null || entity.getId() == null ? null : manager.find(User.class, entity.getId());
    }
}
