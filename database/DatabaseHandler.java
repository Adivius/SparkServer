import java.sql.*;
import java.util.ArrayList;

public class DatabaseHandler {

    public static String TABLE_NAME_USER = "user", TABLE_NAME_MESSAGES = "messages";

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
                "\t\"banned\"\tinteger,\n" +
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

    public static void addUserEntry(User user) throws SQLException {
        Connection connection = getConnection();
        String insertSQL = "insert into user(name, pw_hash, level, banned) values (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, user.NAME.toLowerCase());
        preparedStatement.setString(2, user.PW_HASH);
        preparedStatement.setInt(3, user.LEVEL);
        preparedStatement.setInt(4, user.BANNED);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public static void deleteUserEntry(User user) throws SQLException {
        Connection connection = getConnection();
        String insertSQL = "delete from user where name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, user.NAME.toLowerCase());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public static boolean isUserEntryValidByName(String userName) throws SQLException {
        boolean out = false;
        Connection connection = getConnection();
        String insertSQL = "select * from user where name = ? ";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, userName.toLowerCase());
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

    public static boolean isUserEntryPasswordCorrect(User user) throws SQLException {
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

    public static void setUserEntriesName(User user, String newName) throws SQLException {
        Connection connection = getConnection();
        String insertSQL = "update user set name = ? where name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, newName.toLowerCase());
        preparedStatement.setString(2, user.NAME.toLowerCase());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public static void setUserEntriesLevel(User user, int level) throws SQLException {
        Connection connection = getConnection();
        String insertSQL = "update user set level = ? where name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setInt(1, level);
        preparedStatement.setString(2, user.NAME.toLowerCase());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public static ArrayList<User> getUserEntries() throws SQLException {
        ArrayList<User> out = new ArrayList<>();
        Connection connection = getConnection();
        String insertSQL = "select * from user";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String userName = resultSet.getString("name");
            String pw_hash = resultSet.getString("pw_hash");
            int level = resultSet.getInt("level");
            int banned = resultSet.getInt("banned");
            out.add(new User(userName, pw_hash, level, banned));
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return out;
    }

    public static User getUserEntryByName(String userName) throws SQLException {
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
            int banned = resultSet.getInt("banned");
            out = new User(name, pw_hash, level, banned);
        }
        preparedStatement.close();
        connection.close();
        resultSet.close();
        return out;
    }

    public static void addMessageEntry(Message message) throws SQLException {
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
    }

    public static ArrayList<Message> getMessageEntries() throws SQLException {
        ArrayList<Message> out = new ArrayList<>();
        Connection connection = getConnection();
        String insertSQL = "select * from messages";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String message = resultSet.getString("message");
            String sender = resultSet.getString("sender");
            String recipient = resultSet.getString("recipient");
            long timestamp = resultSet.getLong("timestamp");
            out.add(new Message(message, sender, recipient, timestamp));
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return out;
    }

    public static int getUserEntriesCount() throws SQLException {
        Connection connection = getConnection();
        String insertSQL = "select count(id) from user";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        ResultSet resultSet = preparedStatement.executeQuery();
        int out = resultSet.getInt(1);
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return out;
    }

    public static int getMessageEntriesCount() throws SQLException {
        Connection connection = getConnection();
        String insertSQL = "select count(id) from messages";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        ResultSet resultSet = preparedStatement.executeQuery();
        int out = resultSet.getInt(1);
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return out;
    }

    public static ArrayList<Message> getMessageEntriesFromName(String userName) throws SQLException {
        ArrayList<Message> out = new ArrayList<>();
        Connection connection = getConnection();
        String insertSQL = "select * from messages where recipient = ? or recipient = 'general'";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, userName);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String message = resultSet.getString("message");
            String sender = resultSet.getString("sender");
            String recipient = resultSet.getString("recipient");
            long timestamp = resultSet.getLong("timestamp");
            out.add(new Message(message, sender, recipient, timestamp));
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return out;
    }

}
