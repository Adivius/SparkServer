public class CommandGetLevel extends Command {

    public CommandGetLevel() {
        super("getlevel", "/getlevel <?username>", 0, Security.MEMBER);
    }

    @Override
    public boolean execute(UserConnection userConnection, String[] args) {
        if (!hasPermission(userConnection, SECURITY_LEVEL)) {
            notAllowed(userConnection);
            return false;
        }
        if (args.length == 0) {
            userConnection.sendLog("Your security level: " + userConnection.getSecurityLevel());
            return true;
        }
        String name = args[0].toLowerCase();
        if (!userConnection.getServer().hasUserByName(name)) {
            userConnection.sendLog("UserConnection " + name + " is not online!");
            return false;
        }
        userConnection.sendLog(name + "'s security level: " + userConnection.getServer().getUserByName(name).getSecurityLevel());
        return true;
    }
}
