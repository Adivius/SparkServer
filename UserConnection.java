import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ConcurrentModificationException;

public class UserConnection extends Thread {

    private final SparkServer server;
    private final Socket socket;
    private final String id;
    private PrintWriter writer;
    private BufferedReader reader;
    private User user;
    private String disconnectReason = null;

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
            String response;
            do {
                response = reader.readLine();
            } while (response == null);
            System.out.println(response);
            String[] connectPacket = response.split(PacketIds.SEPARATOR);
            if (!connectPacket[0].equals(Integer.toString(PacketIds.CONNECT))) {
                server.removeUserById(this.getUserId(), "Invalid connect!");
                return;
            }
            if (connectPacket.length < 3) {
                server.removeUserById(this.getUserId(), "Invalid name!");
                return;
            }
            String name = connectPacket[1].toLowerCase();
            if (name.length() > Security.NAME_MAX_LENGTH) {
                server.removeUserById(this.getUserId(), "Name to long!");
                return;
            }
            if (name.isEmpty() || name.matches("\\s")){
                server.removeUserById(this.getUserId(), "Invalid name!");
                return;
            }
            if (Security.nameDenied(name)) {
                server.removeUserById(this.getUserId(), "Name is blocked!");
                return;
            }

            if (DatabaseHandler.userExists(name)){
                if (!DatabaseHandler.checkUserPassword(new User(name, connectPacket[2]))){
                    server.removeUserById(this.getUserId(), "Password was incorrect!");
                    return;
                }
                this.user = DatabaseHandler.getUserByName(name);
                sendLog("Welcome back, "+ server.getUserCount() + " people are online");
            }else {
                User user = new User(name, connectPacket[2]);
                DatabaseHandler.registerUser(user);
                this.user = user;
                sendLog("Welcome " + getUserName() + ", " + server.getUserCount() + " people are online");
                server.broadcastLog("New user connected: " + getUserName(), this);
            }

            loadMessages();

            setUserName(name);


            loop:
            while (!socket.isClosed()) {
                if (!reader.ready() || !socket.isConnected()) {
                    continue;
                }
                response = reader.readLine();
                SparkServer.print(response);
                if (response == null) {
                    SparkServer.print("Response was null");
                    continue;
                }
                String[] packet = response.split(PacketIds.SEPARATOR);
                if (Security.isInvalidInt(packet[0])) {
                    SparkServer.print("Invalid packet ID: " + packet[0]);
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
                            CommandHandler.handleCommand(this, command, args);
                            break;
                        }
                        if (!Security.hasPermission(this, Security.MEMBER)) {
                            continue;
                        }
                        server.broadcastMessage(packetMessage.MESSAGE, null, getUserName());
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

        } catch (IOException | ConcurrentModificationException | SQLException ex) {
            SparkServer.print("Error in UserConnection: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
            reader.close();
            writer.close();
            this.interrupt();
        } catch (IOException e) {
            SparkServer.print("Error in shutdown user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    void send(String bytes) {
        writer.println(bytes);
    }

    public void sendPacket(Packet packet) {
        send(packet.encode());
    }

    public void sendMessage(String message, String sender) {
        String username = sender.isEmpty() ? "System" : sender;
        sendPacket(new PacketMessage(message, username, System.currentTimeMillis()));
    }
    public void sendMessage(String message, String sender, long timestamp) {
        String username = sender.isEmpty() ? "System" : sender;
        sendPacket(new PacketMessage(message, username, timestamp));
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

    public int getSecurityLevel() {
        return user.LEVEL;
    }

    public void setSecurityLevel(int securityLevel) {
        this.user.LEVEL = securityLevel;
    }

    public String getUserName() {
        if (user == null){
            return null;
        }
        return user.NAME;
    }

    public void setUserName(String userName) {
        this.user.NAME = userName;
        sendPacket(new PacketName(userName));
    }

    public void loadMessages(){
        for(Message message : DatabaseHandler.getMessages()){
            if (message.RECIPIENT.equals("general") || message.RECIPIENT.equals(getUserName())){
                sendMessage(message.MESSAGE, message.SENDER, message.TIMESTAMP);
            }
        }
    }
}