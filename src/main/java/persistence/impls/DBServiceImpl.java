package persistence.impls;

import com.zaxxer.hikari.HikariDataSource;
import model.DataSet;
import persistence.DBService;
import persistence.connection.DataSource;
import persistence.dao.JDBC.JDBCUserDAO;

;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

/**
 * Created by entony on 26.12.2017.
 */
public class DBServiceImpl implements DBService {
    private final HikariDataSource dataSource;

    public DBServiceImpl() {
        dataSource = DataSource.get();
    }

    public DBServiceImpl(HikariDataSource dataSource){
        this.dataSource = dataSource;
    }


    public String getLocalStatus() {
        return null;
    }

    public <T extends DataSet> long save(T dataSet) {
        return runInTransaction(connection -> {
            JDBCUserDAO dao = new JDBCUserDAO(connection);
            return dao.save(dataSet);
        });
    }

    public <T extends DataSet> T read(Class<T> clazz, long id) {
        return runInTransaction(connection -> {
            JDBCUserDAO dao = new JDBCUserDAO(connection);
            return dao.read(id, clazz);
        });
    }

    public <T extends DataSet> List<T> readByName(Class<T> clazz, String name) {
        return runInTransaction(connection -> {
            JDBCUserDAO dao = new JDBCUserDAO(connection);
            return dao.readByName(name, clazz);
        });
    }

    public <T extends DataSet> List<T> readAll(Class<T> clazz) {
        return runInTransaction(connection -> {
            JDBCUserDAO dao = new JDBCUserDAO(connection);
            return dao.readAll(clazz);
        });
    }

    @Override
    public void close() throws IOException{
        dataSource.close();
    }

    @SuppressWarnings("Duplicates")
    private <R> R runInTransaction(Function<Connection, R> function) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            R result = function.apply(connection);
            connection.commit();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
