import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DatabaseHandler {

    public static String TABLE_NAME_USER = "user", TABLE_NAME_MESSAGES = "messages";

    public static void init() throws SQLException {
        deleteTable(TABLE_NAME_MESSAGES);
        createTableUser();
        createTableMessages();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:SparkData.db");
    }

    public static void createTableUser() throws SQLException {
        Connection connection = getConnection();
        String insertSQL = "create table if not exists " + TABLE_NAME_USER + " (\n" +
                "\t\"id\"\tinteger not null,\n" +
                "\t\"name\"\ttext,\n" +
                "\t\"pw_hash\"\ttext,\n" +
                "\t\"level\"\tinteger,\n" +
                "\tprimary key(\"id\" autoincrement)\n" +
                ");";
        connection.createStatement().execute(insertSQL);
        connection.close();
    }

    public static void createTableMessages() throws SQLException {
        Connection connection = getConnection();
        String insertSQL = "create table if not exists " + TABLE_NAME_MESSAGES + " (\n" +
                "\t\"id\"\tinteger not null ,\n" +
                "\t\"message\"\ttext,\n" +
                "\t\"sender\"\ttext,\n" +
                "\t\"recipient\"\ttext,\n" +
                "\t\"timestamp\"\tnumeric,\n" +
                "\tprimary key(\"id\" autoincrement)\n" +
                ");";
        connection.createStatement().execute(insertSQL);
        connection.close();
    }

    public static void deleteTable(String tableName) throws SQLException {
        Connection connection = getConnection();
        String insertSQL = "drop table if exists " + tableName;
        connection.createStatement().execute(insertSQL);
        connection.close();
    }

    public static void registerUser(User user) throws SQLException {
        Connection connection = getConnection();
        String insertSQL = "insert into user(name, pw_hash, level) values (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, user.NAME.toLowerCase());
        preparedStatement.setString(2, user.PW_HASH);
        preparedStatement.setInt(3, user.LEVEL);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public static void deleteUser(User user) throws SQLException {
        Connection connection = getConnection();
        String insertSQL = "delete from user where name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, user.NAME.toLowerCase());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public static boolean userExists(String user) throws SQLException {
        boolean out = false;
        Connection connection = getConnection();
        String insertSQL = "select * from user where name = ? ";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, user.toLowerCase());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            if (!resultSet.getString("name").isEmpty()) {
                out = true;
            }
        }
        preparedStatement.close();
        connection.close();
        resultSet.close();
        return out;
    }

    public static boolean checkUserPassword(User user) throws SQLException {
        boolean out = false;
        Connection connection = getConnection();
        String insertSQL = "select * from user where name = ? ";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, user.NAME.toLowerCase());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            if (resultSet.getString("pw_hash").equals(user.PW_HASH)) {
                out = true;
            }
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return out;
    }

    public static void updateName(User user, String newName) throws SQLException {
        Connection connection = getConnection();
        String insertSQL = "update user set name = ? where name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, newName.toLowerCase());
        preparedStatement.setString(2, user.NAME.toLowerCase());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public static Set<User> getUsers() throws SQLException {
        Set<User> out = new HashSet<>();
        Connection connection = getConnection();
        String insertSQL = "select * from user";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(insertSQL);
        while (resultSet.next()) {
            String userName = resultSet.getString("name");
            String pw_hash = resultSet.getString("pw_hash");
            int level = resultSet.getInt("level");
            out.add(new User(userName, pw_hash, level));
        }
        resultSet.close();
        statement.close();
        connection.close();
        return out;
    }

    public static User getUserByName(String userName) throws SQLException {
        User out = null;
        Connection connection = getConnection();
        String insertSQL = "select * from user where name = ? ";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, userName.toLowerCase());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String name = resultSet.getString("name");
            String pw_hash = resultSet.getString("pw_hash");
            int level = resultSet.getInt("level");
            out = new User(name, pw_hash, level);
        }
        preparedStatement.close();
        connection.close();
        resultSet.close();
        return out;
    }


    public static void addMessage(Message message) {
        try {
            Connection connection = getConnection();
            String insertSQL = "insert into messages(message, sender, recipient, timestamp) values (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setString(1, message.MESSAGE);
            preparedStatement.setString(2, message.SENDER.toLowerCase());
            preparedStatement.setString(3, message.RECIPIENT.toLowerCase());
            preparedStatement.setLong(4, message.TIMESTAMP);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Message> getMessages() {
        try {
            ArrayList<Message> out = new ArrayList<>();
            Connection connection = getConnection();
            String insertSQL = "select * from messages";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(insertSQL);
            while (resultSet.next()) {
                String message = resultSet.getString("message");
                String sender = resultSet.getString("sender");
                String recipient = resultSet.getString("recipient");
                long timestamp = resultSet.getLong("timestamp");
                out.add(new Message(message, sender, recipient, timestamp));
            }
            resultSet.close();
            statement.close();
            connection.close();
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
