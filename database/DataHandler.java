import java.sql.SQLException;
import java.util.ArrayList;

public abstract class DataHandler {

    public static void init() {
        try {
/*            DatabaseHandler.deleteTable(DatabaseHandler.TABLE_NAME_USER);
            DatabaseHandler.deleteTable(DatabaseHandler.TABLE_NAME_MESSAGES);*/
            DatabaseHandler.createTableUser();
            DatabaseHandler.createTableMessages();
        } catch (SQLException e) {
            SparkServer.printError(e, "Error staring databaseHandler");
        }
    }

    public static boolean isUserInvalid(User user) {
        try {
            return !DatabaseHandler.isUserEntryValidByName(user.NAME);
        } catch (SQLException e) {
            SparkServer.printError(e, "Error fetching user");
            return true;
        }
    }

    public static void registerUser(User user) {
        try {
            DatabaseHandler.addUserEntry(user);
        } catch (SQLException e) {
            SparkServer.printError(e, "Error adding user");
        }
    }

    /**
     * Registern new User
     */

    public static void registerMessage(Message message) {
        try {
            DatabaseHandler.addMessageEntry(message);
        } catch (SQLException e) {
            SparkServer.printError(e, "Error adding message");
        }
    }

    public static boolean isLoginCorrect(User user) {
        if (isUserInvalid(user)) {
            return false;
        }
        try {
            return DatabaseHandler.isUserEntryPasswordCorrect(user);
        } catch (SQLException e) {
            SparkServer.printError(e, "Error testing users password");
            return false;
        }
    }

    public static User getUserByName(String userName) {
        try {
            return DatabaseHandler.getUserEntryByName(userName.toLowerCase());
        } catch (SQLException e) {
            SparkServer.printError(e, "Error fetching user");
            return null;
        }
    }

    public static ArrayList<Message> getMessagesFromName(String userName) {
        try {
            return DatabaseHandler.getMessageEntriesFromName(userName.toLowerCase());
        } catch (SQLException e) {
            SparkServer.printError(e, "Error fetching messages");
            return new ArrayList<>();
        }
    }

    public static ArrayList<Message> getAllMessages() {
        try {
            return DatabaseHandler.getMessageEntries();
        } catch (SQLException e) {
            SparkServer.printError(e, "Error fetching messages");
            return new ArrayList<>();
        }
    }
}
