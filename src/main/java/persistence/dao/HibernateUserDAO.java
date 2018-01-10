package persistence.dao;

import lombok.AllArgsConstructor;
import model.DataSet;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@AllArgsConstructor

/**
 * Created by entony on 28.12.2017.
 */
public class HibernateUserDAO implements UserDAO {
    private final Session session;


    @Override
    public <T extends DataSet> long save(T dataSet) {
        return (long) session.save(dataSet);
    }

    @Override
    public <T extends DataSet> T read(long id, Class<T> entityClass) {
        return session.load(entityClass, id);
    }

    @Override
    public <T extends DataSet> List<T> readByName(String name, Class<T> entityClass) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(entityClass);
        Root<T> from = criteria.from(entityClass);
        criteria.where(builder.equal(from.get("name"), name));
        Query<T> query = session.createQuery(criteria);

        return query.getResultList();
    }

    @Override
    public <T extends DataSet> List<T> readAll(Class<T> entityClass) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(entityClass);
        criteria.from(entityClass);
        Query<T> query = session.createQuery(criteria);
        return query.getResultList();
    }
}
