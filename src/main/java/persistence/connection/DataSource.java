package persistence.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import model.DataSet;

import java.util.Properties;

/**
 * Created by entony on 26.12.2017.
 */
public class DataSource {
    private static HikariDataSource dataSource;

    private DataSource(){}

    public static HikariDataSource get(){
        if (dataSource == null){
            HikariConfig hikariConfig = new HikariConfig();
            Properties connectionProperties = ConnectionHelper.getConnectionProperties();

            hikariConfig.setJdbcUrl(connectionProperties.getProperty("url"));
            hikariConfig.setDriverClassName(connectionProperties.getProperty("driverName"));
            hikariConfig.setUsername(connectionProperties.getProperty("username"));
            hikariConfig.setPassword(connectionProperties.getProperty("password"));

            hikariConfig.setMaximumPoolSize(10);
            hikariConfig.setAutoCommit(false);
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prerStmtCacheSqlLimit", "1024");

            dataSource = new HikariDataSource(hikariConfig);
        }
        return dataSource;
    }

}
