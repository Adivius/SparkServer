public class CommandKick extends Command {

    public CommandKick() {
        super("kick", "/kick <username>", 1, Security.ADMIN);
    }

    @Override
    public boolean execute(UserConnection userConnection, String[] args) {
        if (!hasPermission(userConnection, SECURITY_LEVEL)) {
            notAllowed(userConnection);
            return false;
        }
        if (args.length < ARGS_LENGTH) {
            userConnection.sendLog("Please enter a userConnection!");
            return false;
        }
        String name = args[0].toLowerCase();
        if (!userConnection.getServer().hasUserByName(name)) {
            userConnection.sendLog("UserConnection " + name + " is not online!");
            return false;
        }
        userConnection.getServer().removeUserById(userConnection.getServer().getUserByName(name).getUserId(), "Kicked by Admin");
        userConnection.sendLog(name + " was kicked!");
        return false;
    }
}
