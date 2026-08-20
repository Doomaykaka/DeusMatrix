package deusmatrix.dao.utils;

import javax.persistence.EntityManager;

public interface SingleResultQueryBody<T> {
    T execute(EntityManager manager);
}
