import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Objects;

public class UserConnection extends Thread {

    private final SparkServer server;
    private final Socket socket;
    private final String id;
    private PrintWriter writer;
    private BufferedReader reader;
    private String disconnectReason = null;
    private boolean connected = false;
    private String connectionName = null;

    public UserConnection(Socket socket, SparkServer server, String id) {
        this.socket = socket;
        this.server = server;
        this.id = id;
    }

    public void run() {
        if (socket.isClosed()) {
            return;
        }
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            startDisconnectTimer();
            String connectionString;
            do {
                connectionString = reader.readLine();
            } while (connectionString == null && !socket.isClosed());
            connected = true;
            String[] connectPacket = connectionString.split(PacketIds.SEPARATOR);
            if (!isConnectionValid(connectPacket)) {
                server.removeUserById(this.getUserId(), "Invalid connect!");
                return;
            }
            String name = connectPacket[1];
            String pw_hash = connectPacket[2];
            User user = new User(name, pw_hash);

            if (DataHandler.isUserInvalid(user)) {
                if (Security.isNameAllowed(name)) {
                    DataHandler.registerUser(user);
                    sendLog("Welcome " + name + ", " + server.getConnectionCount() + " people are online");
                    SparkServer.print("New user registered: " + name);
                    server.broadcastLog("New user registered: " + name, null);
                } else {
                    server.removeUserById(this.getUserId(), "Name is invalid!");
                }

            } else {
                if (DataHandler.isLoginCorrect(user)) {
                    if (Objects.requireNonNull(DataHandler.getUserByName(name.toLowerCase())).BANNED != 0) {
                        server.removeUserById(this.getUserId(), "Banned!");
                    } else {
                        sendLog("Welcome back, " + server.getConnectionCount() + " people are online");
                        SparkServer.print("User logged in: " + name);
                    }

                } else {
                    server.removeUserById(this.getUserId(), "Password was incorrect!");
                }
            }

            connectionName = name;
            sendPacket(new PacketName(name));
            loadMessages();

            loop:
            while (!socket.isClosed()) {
                if (!reader.ready() || !socket.isConnected()) {
                    continue;
                }
                String inputString = reader.readLine();
                if (inputString == null) {
                    continue;
                }
                String[] packet = inputString.split(PacketIds.SEPARATOR);
                if (Security.isInvalidInt(packet[0])) {
                    SparkServer.print("Invalid packet ID: " + packet[0]);
                    server.removeUserById(this.getUserId(), "Invalid packet!");
                    continue;
                }
                int packetID = Integer.parseInt(packet[0]);
                switch (packetID) {
                    case PacketIds.MESSAGE:
                        PacketMessage packetMessage = new PacketMessage(packet);
                        if (packetMessage.MESSAGE == null || packetMessage.MESSAGE.isEmpty()) {
                            break;
                        }
                        if (packetMessage.MESSAGE.startsWith("/")) {
                            String[] commands = packetMessage.MESSAGE.split(" ");
                            String command = commands[0].toLowerCase().substring(1);
                            String[] args = Arrays.copyOfRange(commands, 1, commands.length);
                            //CommandHandler.handleCommand(this, command, args);
                            break;
                        }
                        if (!Security.hasPermission(this, Security.MEMBER)) {
                            continue;
                        }
                        server.broadcastMessage(packetMessage.MESSAGE, connectionName, packetMessage.RECIPIENT);
                        break;
                    case PacketIds.DISCONNECT:
                        PacketDisconnect packetDisconnect = new PacketDisconnect(packet);
                        disconnectReason = packetDisconnect.REASON;
                        break loop;
                }
            }
            if (!socket.isClosed()) {
                server.removeUserById(id, disconnectReason);
            }


        } catch (IOException | ConcurrentModificationException ex) {
            SparkServer.print("Error in UserConnection: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            this.interrupt();
            if (!socket.isClosed()) {
                socket.close();
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            SparkServer.print("Error in shutdown user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isConnectionValid(String[] connectPacket) {
        String packet = String.join("", connectPacket);
        if (!connectPacket[0].equals(Integer.toString(PacketIds.CONNECT))) {
            return false;
        }
        if (connectPacket.length < 3) {
            return false;
        }
        return !packet.matches("\\s");
    }

    private void send(String bytes) {
        writer.println(bytes);
    }

    public void sendPacket(Packet packet) {
        send(packet.encode());
    }

    public void sendMessage(Message message) {
        sendPacket(new PacketMessage(message.MESSAGE, message.SENDER, message.TIMESTAMP, message.RECIPIENT));
    }

    public void sendLog(String log) {
        sendPacket(new PacketLog(log));
    }

    public String getUserId() {
        return id;
    }

    public SparkServer getServer() {
        return server;
    }

    public User getUser() {
        return DataHandler.getUserByName(connectionName);
    }

    public void loadMessages() {
        for (Message message : DataHandler.getMessagesFromName(connectionName)) {
            sendMessage(message);
        }
    }

    private void startDisconnectTimer() {
        class TimerThread extends Thread {
            final UserConnection userConnection;

            public TimerThread(UserConnection userConnection) {
                this.userConnection = userConnection;
            }

            public void run() {
                try {
                    sleep(2000);
                    if (!userConnection.connected) {
                        userConnection.server.removeUserById(userConnection.getUserId(), "Timeout");
                    }
                } catch (InterruptedException e) {
                    userConnection.server.removeUserById(userConnection.getUserId(), "Timeout");
                }
            }
        }
        new TimerThread(this).start();
    }
}