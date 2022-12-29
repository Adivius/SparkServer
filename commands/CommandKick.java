public class CommandKick extends Command {

    public CommandKick() {
        super("kick", "/kick <username>", 1, Security.ADMIN);
    }

    @Override
    public boolean execute(User user, String[] args) {
        if (!hasPermission(user, SECURITY_LEVEL)) {
            notAllowed(user);
            return false;
        }
        if (args.length < ARGS_LENGTH) {
            user.sendLog("Please enter a user!");
            return false;
        }
        String name = args[0].toLowerCase();
        if (!user.getServer().hasUserByName(name)) {
            user.sendLog("User " + name + " is not online!");
            return false;
        }
        user.getServer().removeUserById(user.getServer().getUserByName(name).getUserId(), "Kicked by Admin");
        user.sendLog(name + " was kicked!");
        return false;
    }
}
