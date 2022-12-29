public class CommandGetLevel extends Command {

    public CommandGetLevel() {
        super("getlevel", "/getlevel <?username>", 0, Security.MEMBER);
    }

    @Override
    public boolean execute(User user, String[] args) {
        if (!hasPermission(user, SECURITY_LEVEL)) {
            notAllowed(user);
            return false;
        }
        if (args.length == 0) {
            user.sendLog("Your security level: " + user.getSecurityLevel());
            return true;
        }
        String name = args[0].toLowerCase();
        if (!user.getServer().hasUserByName(name)) {
            user.sendLog("User " + name + " is not online!");
            return false;
        }
        user.sendLog(name + "'s security level: " + user.getServer().getUserByName(name).getSecurityLevel());
        return true;
    }
}
