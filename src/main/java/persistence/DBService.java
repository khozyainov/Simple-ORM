package persistence;

import model.DataSet;
import model.UserDataSet;

import java.util.List;

/**
 * Created by entony on 26.12.2017.
 */
public interface DBService extends AutoCloseable{
    String getLocalStatus();

    <T extends DataSet> long save(T dataSet);

    <T extends DataSet> T read(Class<T> clazz, long id);

    <T extends DataSet> List<T> readByName(Class<T> clazz, String name);

    <T extends DataSet> List<T> readAll(Class<T> clazz);
}
