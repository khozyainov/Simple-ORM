package persistence.dao;

import model.DataSet;

import java.util.List;

/**
 * Created by entony on 28.12.2017.
 */
public interface UserDAO {
    <T extends DataSet> long save(T dataSet);

    <T extends DataSet> T read(long id, Class<T> entityClass);

    <T extends DataSet> List<T> readByName(String name, Class<T> entityClass);

    <T extends DataSet> List<T> readAll(Class<T> entityClass);
}
