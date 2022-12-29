public class CommandSetLevel extends Command {

    public CommandSetLevel() {
        super("setlevel", "/setlevel <username> <level>", 2, Security.ADMIN);
    }

    @Override
    public boolean execute(User user, String[] args) {
        if (!hasPermission(user, SECURITY_LEVEL)) {
            notAllowed(user);
            return false;
        }
        if (args.length < ARGS_LENGTH) {
            user.sendLog("Please enter a user and a level!");
            return false;
        }
        String name = args[0].toLowerCase();
        if (!user.getServer().hasUserByName(name)) {
            user.sendLog("User " + name + " is not online!");
            return false;
        }
        if (Security.isInvalidInt(args[1])) {
            user.sendLog("Please enter a valid level!");
            return false;
        }

        int level = Integer.parseInt(args[1]);
        User change = user.getServer().getUserByName(name);

        if (!hasPermission(user, level)) {
            notAllowed(user);
            return false;
        }
        change.setSecurityLevel(level);
        change.sendLog("Your security level was set to: " + level);
        user.sendLog("Security level of " + name + " was set to " + level);
        return true;
    }
}
