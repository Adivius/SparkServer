public class CommandHelp extends Command {


    public CommandHelp() {
        super("help", "/help <?command>", 0, Security.VISITOR);
    }

    @Override
    public boolean execute(UserConnection userConnection, String[] args) {
        if (!hasPermission(userConnection, SECURITY_LEVEL)) {
            notAllowed(userConnection);
            return false;
        }
        if (args.length == 0) {
            userConnection.sendLog(CommandHandler.getHelp());
        } else {
            if (!CommandHandler.commands.containsKey(args[0].toLowerCase())) {
                userConnection.sendLog("Command " + args[0].toLowerCase() + " was invalid!");
                return false;
            }
            userConnection.sendLog(CommandHandler.commands.get(args[0]).USAGE);
        }
        return true;
    }
}
