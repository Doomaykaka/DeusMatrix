package deusmatrix.dao.utils;

import javax.persistence.EntityManager;

public interface QueryBody {
    void execute(EntityManager manager);
}
