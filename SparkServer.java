import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class SparkServer extends Thread {

    private final int port;
    private final HashMap<String, UserConnection> connections = new HashMap<>();
    private ServerSocket serverSocket;

    public SparkServer(int port) {
        this.port = port;
    }

    public static void print(String message) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm: ");
        String time = simpleDateFormat.format(System.currentTimeMillis());
        System.out.println(time + message);
    }

    public static void printError(Exception exception, String errorMessage) {
        print(errorMessage + ": " + exception.getMessage());
        exception.printStackTrace();
    }

    public static void printMessage(Message message) {
        print(message.SENDER + ": " + message.MESSAGE);
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            DataHandler.init();
            print("Chat ServerMain is listening on port " + port);
        } catch (IOException e) {
            print("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
        new WriteThread(this).start();
        for (Message message : DataHandler.getAllMessages()) {
            printMessage(message);
        }
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                registerConnection(socket);
            }
        } catch (IOException ignored) {
        } finally {
            print("Server shut down!");
        }
    }

    public void registerConnection(Socket socket) {
        String ip = String.valueOf(socket.getInetAddress()).replace("/", "");
        int port = socket.getPort();
        String id = ip + PacketIds.SEPARATOR + port;
        UserConnection newUserConnection = new UserConnection(socket, this, id);
        print("New connection: " + id);
        connections.put(id, newUserConnection);
        newUserConnection.start();
    }

    public void broadcastMessage(String messageText, String sender, String recipient) {

        long millis = System.currentTimeMillis();
        Message message = new Message(messageText, sender, recipient, millis);
        DataHandler.registerMessage(message);
        printMessage(message);

        for (UserConnection userConnection : connections.values()) {
            if (recipient.equals(Security.STANDARD_RECIPIENT) || userConnection.getUser().NAME.equals(recipient)) {
                userConnection.sendMessage(message);
            }
        }
    }

    public void broadcastLog(String log, UserConnection excludeUserConnection) {
        for (UserConnection userConnection : connections.values()) {
            if (userConnection != excludeUserConnection) {
                userConnection.sendLog(log);
            }
        }
    }

    int getConnectionCount() {
        return connections.size();
    }

    public void removeUserById(String id, String reason) {
        UserConnection removeUserConnection = connections.get(id);
        removeUserConnection.sendPacket(new PacketDisconnect(reason));
        removeUserConnection.shutdown();
        print("UserConnection disconnected: " + id + ": " + reason);
        connections.remove(id);
    }

    public void kickAll(UserConnection excludeUserConnection) {
        for (UserConnection userConnection : connections.values()) {
            if (excludeUserConnection != userConnection) {
                userConnection.sendPacket(new PacketDisconnect("Kicked by Admin"));
                userConnection.shutdown();
                connections.remove(userConnection.getUserId());
            }
        }
        if (excludeUserConnection != null) {
            excludeUserConnection.sendLog("All user kicked excepted!");
        }
        print("All user kicked!");
    }

    public void shutdown() {
        for (UserConnection userConnection : connections.values()) {
            removeUserById(userConnection.getUserId(), "Server shutdown");
        }
        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            printError(e, "Error closing server");
        }
        this.interrupt();
        System.exit(0);
    }
}