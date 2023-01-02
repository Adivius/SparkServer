import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SparkServer extends Thread {

    private final int port;
    private final HashMap<String, UserConnection> users = new HashMap<>();
    private ServerSocket serverSocket;

    public SparkServer(int port) {
        this.port = port;
    }

    public static void print(String message) {
        System.out.println(message);
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            CommandHandler.init();
            DatabaseHandler.init();
            print("Chat ServerMain is listening on port " + port);
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                addUser(socket);
            }
        } catch (Exception e) {
            SparkServer.print("Error in starting Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void broadcast(String message, UserConnection excludeUserConnection, String sender) {
        for (Map.Entry<String, UserConnection> userPair : users.entrySet()) {
            if (userPair.getValue() != excludeUserConnection) {
                userPair.getValue().sendMessage(message, sender);
            }
        }
    }

    public void broadcast(Packet packet, UserConnection excludeUserConnection) {
        for (Map.Entry<String, UserConnection> userPair : users.entrySet()) {
            if (userPair.getValue() != excludeUserConnection) {
                userPair.getValue().sendPacket(packet);
            }
        }
    }

    public String getUserNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Map.Entry<String, UserConnection> userPair : users.entrySet()) {
            String userName = userPair.getValue().getUserName();
            if (userName != null) {
                names.add(userName);
            }
        }
        return names.size() + " Users: " + names;
    }

    int getUserCount() {
        return users.size();
    }

    public void removeUserById(String id, String reason) {
        UserConnection removeUserConnection = users.get(id);
        removeUserConnection.sendPacket(new PacketDisconnect(reason));
        removeUserConnection.shutdown();
        print("UserConnection disconnected: " + id + ": " + reason);
        users.remove(id);
    }

    public UserConnection getUserByName(String name) {
        UserConnection userConnection = null;
        for (Map.Entry<String, UserConnection> userPair : users.entrySet()) {
            String userName = userPair.getValue().getUserName();
            if (userName == null) {
                continue;
            }
            if (userName.equals(name.toLowerCase())) {
                userConnection = userPair.getValue();
            }
        }
        return userConnection;
    }

    public void kickAll(UserConnection excludeUserConnection){
        ArrayList<UserConnection> removeUserConnections = new ArrayList<>();
        for (Map.Entry<String, UserConnection> userPair : users.entrySet()) {
            UserConnection removeUserConnection = userPair.getValue();
            if (!excludeUserConnection.equals(removeUserConnection)){
                removeUserConnection.sendPacket(new PacketDisconnect("Kicked by Admin"));
                removeUserConnection.shutdown();
                removeUserConnections.add(removeUserConnection);
            }
        }
        for (UserConnection userConnection : removeUserConnections){
            users.remove(userConnection.getUserId());
        }
        print("All user kicked excepted " + excludeUserConnection.getUserName());
    }

    public boolean hasUserByName(String name){
        try {
            return DatabaseHandler.userExists(name.toLowerCase());
        } catch (SQLException e) {
            SparkServer.print("Error in getting user: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    void addUser(Socket socket) {

        String ip = String.valueOf(socket.getInetAddress()).replace("/", "");
        int port = socket.getPort();
        String id = ip + PacketIds.SEPARATOR + port;
        UserConnection newUserConnection = new UserConnection(socket, this, id);
        print("New user connected: " + id);
        users.put(id, newUserConnection);
        newUserConnection.start();
    }

    public void shutdown() {
        for (String user : users.keySet()) {
            removeUserById(user, "Server shutdown");
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.interrupt();
            System.exit(0);
        }
    }

    public HashMap<String, UserConnection> getUsers() {
        return users;
    }

}