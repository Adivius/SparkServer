public class CommandHelp extends Command {


    public CommandHelp() {
        super("help", "/help <?command>", 0, Security.VISITOR);
    }

    @Override
    public boolean execute(User user, String[] args) {
        if (!hasPermission(user, SECURITY_LEVEL)) {
            notAllowed(user);
            return false;
        }
        if (args.length == 0) {
            user.sendLog(CommandHandler.getHelp());
        } else {
            if (!CommandHandler.commands.containsKey(args[0].toLowerCase())) {
                user.sendLog("Command " + args[0].toLowerCase() + " was invalid!");
                return false;
            }
            user.sendLog(CommandHandler.commands.get(args[0]).USAGE);
        }
        return true;
    }
}
