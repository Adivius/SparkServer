import java.util.Arrays;

public class CommandMsg extends Command {

    public CommandMsg() {
        super("msg", "/msg <username> <message>", 2, Security.MEMBER);
    }

    @Override
    public boolean execute(UserConnection userConnection, String[] args) {
        if (!hasPermission(userConnection, SECURITY_LEVEL)) {
            notAllowed(userConnection);
            return false;
        }
        if (args.length < ARGS_LENGTH) {
            userConnection.sendLog("Please enter a userConnection and a message!");
            return false;
        }
        String name = args[0].toLowerCase();
        if (!userConnection.getServer().hasUserByName(name)) {
            userConnection.sendLog("UserConnection " + name + " is not online!");
            return false;
        }

        UserConnection recipient = userConnection.getServer().getUserByName(name);
        String[] message = Arrays.copyOfRange(args, 1, args.length);
        recipient.sendMessage(userConnection.getUserName() + " whispers: " + String.join(" ", message), "System");
        return true;
    }
}
