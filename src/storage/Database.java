package storage;

import lombok.NonNull;
import lombok.SneakyThrows;
import utils.DatabaseUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Database {
    private static Database instance;
    private static Connection connection;

    private Database() {}

    @SneakyThrows({SQLException.class, ClassNotFoundException.class})
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
            connection = DriverManager.getConnection(DatabaseUtils.CONNECTION_URL, DatabaseUtils.USER_NAME, DatabaseUtils.PASSWORD);
            Class.forName(DatabaseUtils.DRIVER);
        }
        return instance;
    }

    @SneakyThrows({SQLException.class, NoSuchMethodException.class, IllegalAccessException.class, InvocationTargetException.class})
    public <T> void insertOrUpdate(@NonNull T entityClass) {
        List<String> columnNames = new ArrayList<>();
        List<String> values = new ArrayList<>();
        reflectionFillingToDB(entityClass, columnNames, values, null);
        if (entityClass.getClass().getDeclaredMethod("getId").invoke(entityClass) == null) connection.createStatement().executeUpdate(String.format(DatabaseUtils.INSERT_REQUEST, entityClass.getClass().getSimpleName(), String.join(",", columnNames), String.join(",", values)));
        else {
            List<String> updateString = new ArrayList<>();
            for (int i = 0; i < columnNames.size(); i++) updateString.add(String.format("%s = %s", columnNames.get(i), values.get(i)));
            connection.createStatement().executeUpdate(String.format(DatabaseUtils.UPDATE_REQUEST, entityClass.getClass().getSimpleName(), String.join(",", updateString), Integer.parseInt(values.get(0))));
        }
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows({SQLException.class, NoSuchMethodException.class, IllegalAccessException.class, InvocationTargetException.class})
    public <T> TreeSet<T> select(@NonNull T entityClass) {
        List<String> columnNames = new ArrayList<>();
        List<Class<?>> columnTypes = new ArrayList<>();
        List<String> values = new ArrayList<>();
        TreeSet<T> selectedRecords = new TreeSet<>();
        String condition = "";
        reflectionFillingToDB(entityClass, columnNames, values, columnTypes);

        if (!(Collections.frequency(values, values.get(0)) == values.size())) {
            List<String> cond = new ArrayList<>();
            for (int i = 0; i < values.size(); i++)
                if (values.get(i) != null) cond.add(String.format("%s = %s", columnNames.get(i), values.get(i)));
            condition = String.format(DatabaseUtils.SELECT_OPTIONS, String.join(" AND ", cond));
        }
        String sqlRequest = String.format(DatabaseUtils.SELECT_REQUEST, entityClass.getClass().getSimpleName(), condition);

        try (ResultSet resultSet = connection.createStatement().executeQuery(sqlRequest)) {
            while (resultSet.next()) {
                T record = (T) entityClass.getClass().getDeclaredMethod("builder").invoke(entityClass);
                for (int i = 0; i < columnNames.size(); i++) {
                    String type = columnTypes.get(i).getSimpleName();
                    if (type.equals("Integer")) type = "Int";
                    Object resultSetOutput = resultSet.getClass().getDeclaredMethod(String.format("get%s", type), String.class).invoke(resultSet, columnNames.get(i));
                    if (type.equals("String") && resultSetOutput != null) record.getClass().getDeclaredMethod(columnNames.get(i), columnTypes.get(i)).invoke(record, resultSetOutput.toString().trim());
                    else record.getClass().getDeclaredMethod(columnNames.get(i), columnTypes.get(i)).invoke(record, resultSetOutput);
                }
                record = (T) record.getClass().getDeclaredMethod("build").invoke(record);
                selectedRecords.add(record);
            }
        }

        return selectedRecords;
    }

    @SneakyThrows(SQLException.class)
    public <T> void delete(@NonNull Class<T> entityClass, @NonNull Integer id) {
        connection.createStatement().executeUpdate(String.format(DatabaseUtils.DELETE_REQUEST, entityClass.getSimpleName(), id));
    }

    public <T> void reflectionFillingToDB(@NonNull T entityClass, @NonNull List<String> columnNames, @NonNull List<String> values, List<Class<?>> columnTypes) {
        Arrays.stream(entityClass.getClass().getDeclaredFields()).forEach(field -> {
            try {
                Object value = entityClass.getClass().getDeclaredMethod(String.format("get%s%s", field.getName().substring(0, 1).toUpperCase(), field.getName().substring(1))).invoke(entityClass);
                if (value == null) {
                    if (columnTypes == null) return;
                    else values.add(null);
                }
                else {
                    if (value instanceof Integer) values.add(value.toString());
                    else values.add(String.format("'%s'", value));
                }
                columnNames.add(field.getName());
                if (columnTypes != null) columnTypes.add(field.getType());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {}
        });
    }
}