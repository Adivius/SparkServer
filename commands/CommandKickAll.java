public class CommandKickAll extends Command {

    public CommandKickAll() {
        super("kickall", "/kickall", 0, Security.OPERATOR);
    }

    @Override
    public boolean execute(UserConnection userConnection, String[] args) {
        if (!hasPermission(userConnection, SECURITY_LEVEL)) {
            notAllowed(userConnection);
            return false;
        }
        try {
            userConnection.getServer().kickAll(userConnection);
            userConnection.sendLog("Kicked all userConnection!");
            return true;
        } catch (Exception e) {
            SparkServer.print("Error disconnecting all userConnection: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
