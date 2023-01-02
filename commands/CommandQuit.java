public class CommandQuit extends Command {

    public CommandQuit() {
        super("quit", "/quit", 0, Security.VISITOR);
    }

    @Override
    public boolean execute(UserConnection userConnection, String[] args) {
        if (!hasPermission(userConnection, SECURITY_LEVEL)) {
            notAllowed(userConnection);
            return false;
        }
        userConnection.getServer().removeUserById(userConnection.getUserId(), "Disconnected");
        return true;
    }
}
