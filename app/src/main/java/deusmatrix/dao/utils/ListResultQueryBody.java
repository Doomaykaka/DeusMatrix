package deusmatrix.dao.utils;

import java.util.List;
import javax.persistence.EntityManager;

public interface ListResultQueryBody<T> {
    List<T> execute(EntityManager manager);
}
