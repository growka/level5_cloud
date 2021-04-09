package handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

public class SqlHandler {

    private static Connection connection;
    private static PreparedStatement psGetFolderByLoginAndPassword;
    private static PreparedStatement psCreateNewUser;
    private static PreparedStatement psGetClientData;

    private static final Logger LOG = LoggerFactory.getLogger(SqlHandler.class);

    //Подключение к БД
    public static boolean connect() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/user_db?useUnicode=true&serverTimezone=UTC&useSSL=false", "root", "123456");



//            connection = DriverManager.getConnection(
//                    ServerProperties.getInstance().getProperties("database"),
//                    ServerProperties.getInstance().getProperties("userdb"),
//                    ServerProperties.getInstance().getProperties("userpsw")
            //);
            psCreateNewUser = connection.prepareStatement("INSERT INTO users (login, password,folder) values (?,?,?);");
            psGetFolderByLoginAndPassword = connection.prepareStatement("SELECT folder FROM users WHERE login = ? AND password = ?;");
            psGetClientData = connection.prepareStatement("SELECT * FROM users WHERE login = ? AND password = ?;");

            LOG.debug("Соединение с базой успешно установлено");

            return true;

        } catch (Exception e) {
            //e.printStackTrace();
            LOG.debug("Не удалось установить соединение с базой:", e);
            return false;
        }
    }
    //Забираем папку по логину и паролю.
    public static String isAuthClient(String login, String password) {
        String folder = null;
        try {
            psGetFolderByLoginAndPassword.setString(1, login);
            psGetFolderByLoginAndPassword.setString(2, password);
            ResultSet resultSet = psGetFolderByLoginAndPassword.executeQuery();
            if (resultSet.next()) {
                folder = resultSet.getString(1);
            }
            LOG.debug("Пользователь успешно авторизован папка: " + folder);
            resultSet.close();
        }catch (SQLException e){
            LOG.debug("Авторизация неуспешна ", e);
        }
        return folder;
    }
    //Создаем нового пользователя.
    public static void createNewUser(String login, String password, String folder) {
        try {
            psCreateNewUser.setString(1, login);
            psCreateNewUser.setString(2, password);
            psCreateNewUser.setString(3, folder);
            psCreateNewUser.executeUpdate();

            LOG.debug("Успешно создан пользователь " + login + " " + password + " " + " " + folder);
        }catch (SQLException e){
            LOG.debug("Создать пользователя не удалось: ", e);
        }
    }

//    //Авторизация пользователя.
//    public static boolean isAuthClient(String login, String password){
//        try {
//            psGetClientData.setString(1, login);
//            psGetClientData.setString(2, password);
//            ResultSet resultSet = psGetClientData.executeQuery();
//            if (resultSet.next()){
//                return true;
//            }
//            resultSet.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public static void disconnect() {
        try {
            psGetFolderByLoginAndPassword.close();
            psCreateNewUser.close();
            LOG.debug("Prepared statement close successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.debug("Ошибка при закрытии подготовленных запросов", e);
        }
        try {
            connection.close();
            LOG.debug("Соединение с базой успешно закрыто");
        } catch (SQLException e) {
            LOG.debug("Ошибка при закрытии соедининения с базой", e);
        }
    }
}