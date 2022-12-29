public class CommandKickAll extends Command {

    public CommandKickAll() {
        super("kickall", "/kickall", 0, Security.OPERATOR);
    }

    @Override
    public boolean execute(User user, String[] args) {
        if (!hasPermission(user, SECURITY_LEVEL)) {
            notAllowed(user);
            return false;
        }
        try {
            user.getServer().kickAll(user);
            user.sendLog("Kicked all user!");
            return true;
        } catch (Exception e) {
            SparkServer.print("Error disconnecting all user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
