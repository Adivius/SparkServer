public class CommandSetLevel extends Command {

    public CommandSetLevel() {
        super("setlevel", "/setlevel <username> <level>", 2, Security.ADMIN);
    }

    @Override
    public boolean execute(UserConnection userConnection, String[] args) {
        if (!hasPermission(userConnection, SECURITY_LEVEL)) {
            notAllowed(userConnection);
            return false;
        }
        if (args.length < ARGS_LENGTH) {
            userConnection.sendLog("Please enter a userConnection and a level!");
            return false;
        }
        String name = args[0].toLowerCase();
        if (!userConnection.getServer().hasUserByName(name)) {
            userConnection.sendLog("UserConnection " + name + " is not online!");
            return false;
        }
        if (Security.isInvalidInt(args[1])) {
            userConnection.sendLog("Please enter a valid level!");
            return false;
        }

        int level = Integer.parseInt(args[1]);
        UserConnection change = userConnection.getServer().getUserByName(name);

        if (!hasPermission(userConnection, level)) {
            notAllowed(userConnection);
            return false;
        }
        change.setSecurityLevel(level);
        change.sendLog("Your security level was set to: " + level);
        userConnection.sendLog("Security level of " + name + " was set to " + level);
        return true;
    }
}
