package persistence.dao.JDBC;

import model.DataSet;
import persistence.dao.UserDAO;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by entony on 26.12.2017.
 */
public class JDBCUserDAO implements UserDAO {
    private Connection connection;

    public JDBCUserDAO(Connection connection){
        this.connection = connection;
    }

    @Override
    public <T extends DataSet> long save(T dataSet){
        try (Statement stmt = connection.createStatement()){
            String query = generateInsertQuery(dataSet);
            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet result = stmt.getGeneratedKeys();
            result.next();
            return result.getLong("id");
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends DataSet> T read(long id, Class<T> entityClass){
        String query = generateSelectByIdQuery(entityClass, id);
        List<T> result = executeLoad(query,entityClass);
        if (!result.isEmpty()){
            return result.get(0);
        }
        return null;
    }

    @Override
    public <T extends DataSet> List<T> readByName(String name, Class<T> entityClass){
        String query = generateSelectByName(entityClass, name);
        List<T> result = executeLoad(query,entityClass);
        if (!result.isEmpty()){
            return result;
        }
        return null;
    }

    @Override
    public <T extends DataSet> List<T> readAll(Class<T> entityClass){
        String tableName = entityClass.getAnnotation(Table.class).name();
        String query = "SELECT * FROM \"" + tableName + "\"";
        return executeLoad(query, entityClass);
    }

    private <T extends DataSet> List<T> executeLoad(String query, Class<T> entityClass){
        Map<String, Field> columnFieldsMap = getColumnFields(entityClass);
        List<T> resultList = new ArrayList<T>();
        try(Statement stmt = connection.createStatement()){
            ResultSet result = stmt.executeQuery(query);
            while (result.next()){
                T entity = entityClass.newInstance();
                for (Map.Entry<String,Field> item: columnFieldsMap.entrySet()) {
                    Field field = item.getValue();
                    field.setAccessible(true);
                    field.set(entity, result.getObject(item.getKey()));
                }
                resultList.add(entity);
            }
            return resultList;

        } catch (SQLException | IllegalAccessException | InstantiationException e){
            throw new RuntimeException(e);
        }
    }

    private <T extends DataSet> String generateSelectByName(Class<T> entityClass, String name){
        String tableName = entityClass.getAnnotation(Table.class).name();
        return "SELECT * FROM \"" + tableName + "\" WHERE NAME = " + name;
    }

    private <T extends DataSet> String generateSelectByIdQuery( Class<T> entityClass, long id) {
        String tableName = entityClass.getAnnotation(Table.class).name();
        return "SELECT * FROM \"" + tableName + "\" WHERE ID = " + id;
    }

    private <T extends DataSet> String generateInsertQuery(T dataSet){
        String tableName = dataSet.getClass().getAnnotation(Table.class).name();
        Map<String, Field> columnFieldsMap = getColumnFields(dataSet.getClass());
        columnFieldsMap.remove("id");

        Set<String> columns = columnFieldsMap.keySet();
        List<String> values = new ArrayList<>();

        try{
            for (Map.Entry<String, Field> item: columnFieldsMap.entrySet()){
                Field field = item.getValue();
                field.setAccessible(true);

                String value = String.valueOf(field.get(dataSet));
                if (String.class.equals(field.getType())){
                    value = "'" + value + "'";
                }
                values.add(value);
            }
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
        return "INSERT INTO \"" + tableName + "\""
                + String.format(" (%s) ", String.join(", ", columns))
                + " values " + String.format(" (%s) ", String.join(", ", values));
    }

    private LinkedHashMap<String, Field> getColumnFields(Class<?> clazz){
        LinkedHashMap<String, Field> fields = new LinkedHashMap<>();

        Class<?> c = clazz;
        while (!c.equals(Object.class)){
            for(Field field: c.getDeclaredFields()){
                if(field.isAnnotationPresent(Column.class)){
                    fields.put(field.getAnnotation(Column.class).name(), field);
                }
            }
            c = c.getSuperclass();
        }
        return fields;
    }
}
