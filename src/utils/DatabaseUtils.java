package utils;

public class DatabaseUtils {
    private DatabaseUtils() {}
    public static final String IP = "localhost";
    public static final int PORT = 5432;
    public static final String DATABASE_NAME = "postgres";
    public static final String USER_NAME = "postgres";
    public static final String PASSWORD = "postgres";

    //Предметная область
    // Добавить лекарствам поля
    // У клиентов клиенты и логины скрыть
    // Что с ролью при вставке доктора?
    // Комментарий пациента
    // История посещения пациента
    // Фильтр для просмотра по датам
    // Отсылать на почту информации по приему
    // В приеме - вместо комментария - рекомендация
    // Закрыть пароли у админа
    // Шифрование пароля в бд
    // Убрать пароль в таблицах
    // Изменить цвета
    // Валидация полей
    //Сделать сервер


    public static final String CONNECTION_URL = String.format("jdbc:postgresql://%s:%s/%s", IP, PORT, DATABASE_NAME);
    //public static final String CONNECTION_URL = "jdbc:postgresql://2256-79-132-103-48.ngrok-free.app/postgres";
    public static final String DRIVER = "org.postgresql.Driver";

    public static final String INSERT_REQUEST = "INSERT INTO %s (%s) VALUES (%s)";
    public static final String UPDATE_REQUEST = "UPDATE %s SET %s WHERE id = %o";
    public static final String DELETE_REQUEST = "DELETE FROM %s WHERE id = %o";
    public static final String SELECT_REQUEST = "SELECT * FROM %s %s";
    public static final String SELECT_OPTIONS = "WHERE %s";
}