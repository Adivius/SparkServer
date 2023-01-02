public class CommandName extends Command {

    public CommandName() {
        super("name", "/name <name>", 1, Security.ADMIN);
    }

    @Override
    public boolean execute(UserConnection userConnection, String[] args) {
        if (!hasPermission(userConnection, SECURITY_LEVEL)) {
            notAllowed(userConnection);
            return false;
        }
        if (args.length == 0) {
            userConnection.sendLog("Your name: " + userConnection.getUserName());
            return false;
        }
        String newName = args[0].toLowerCase();
        SparkServer server = userConnection.getServer();
        if (server.hasUserByName(newName)) {
            userConnection.sendLog("This name is occupied!");
            return false;
        }
        if (Security.nameDenied(newName)) {
            userConnection.sendLog("This name is blocked!");
            return false;
        }
        server.broadcastLog(userConnection.getUserName() + "'s name was changed to " + newName, null);
        userConnection.setUserName(newName);
        return true;
    }
}
