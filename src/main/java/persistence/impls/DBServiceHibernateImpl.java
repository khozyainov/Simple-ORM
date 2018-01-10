package persistence.impls;

import model.AddressDataSet;
import model.DataSet;
import model.PhoneDataSet;
import model.UserDataSet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import persistence.DBService;
import persistence.connection.ConnectionHelper;
import persistence.dao.HibernateUserDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

/**
 * Created by entony on 28.12.2017.
 */
public class DBServiceHibernateImpl implements DBService {
    private final SessionFactory sessionFactory;

    public DBServiceHibernateImpl(){
        Configuration configuration = createConfiguration();
        sessionFactory = createSessionFactory(configuration);
    }

    public DBServiceHibernateImpl(Configuration configuration){
        sessionFactory = createSessionFactory(configuration);
    }

    private Configuration createConfiguration() {
        Configuration configuration = new Configuration();

        Properties properties = ConnectionHelper.getConnectionProperties();

        configuration.setProperty("hibernate.dialect", properties.getProperty("dialect"));
        configuration.setProperty("hibernate.connection.driver_class", properties.getProperty("driverName"));
        configuration.setProperty("hibernate.connection.url", properties.getProperty("url"));
        configuration.setProperty("hibernate.connection.username", properties.getProperty("username"));
        configuration.setProperty("hibernate.connection.password", properties.getProperty("password"));

        configuration.setProperty("hibernate.show_sql", "false");
        configuration.setProperty("hibernate.hbm2ddl.auto", properties.getProperty("hbm2ddl"));
        configuration.setProperty("hibernate.format_sql", "true");
        configuration.setProperty("hibernate.enable_lazy_load_no_trans", "true");
        configuration.setProperty("hibernate.connection.useSSL", "false");

        configuration.addAnnotatedClass(UserDataSet.class);
        configuration.addAnnotatedClass(PhoneDataSet.class);
        configuration.addAnnotatedClass(AddressDataSet.class);

        return configuration;
    }

    private SessionFactory createSessionFactory(Configuration configuration){
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();

        return configuration.buildSessionFactory(serviceRegistry);
    }
    @Override
    public String getLocalStatus() {
        return runInSession(session ->
            session.getTransaction().getStatus().name());
    }

    @Override
    public <T extends DataSet> long save(T dataSet) {
        return runInSession(session -> {
            HibernateUserDAO dao = new HibernateUserDAO(session);
            return dao.save(dataSet);
        });
    }

    @Override
    public <T extends DataSet> T read(Class<T> clazz, long id) {
        return runInSession(session -> {
            HibernateUserDAO dao = new HibernateUserDAO(session);
            return dao.read(id, clazz);
        });
    }

    @Override
    public <T extends DataSet> List<T> readByName(Class<T> clazz, String name) {
        return runInSession(session -> {
            HibernateUserDAO dao = new HibernateUserDAO(session);
            return dao.readByName(name, clazz);
        });
    }

    @Override
    public <T extends DataSet> List<T> readAll(Class<T> clazz) {
        return runInSession(session -> {
            HibernateUserDAO dao = new HibernateUserDAO(session);
            return dao.readAll(clazz);
        });
    }

    @Override
    public void close() throws Exception {
        sessionFactory.close();
    }

    @SuppressWarnings("Duplicates")
    private <R> R runInSession(Function<Session, R> function) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            R result = function.apply(session);
            transaction.commit();
            return result;
        }
    }
}
