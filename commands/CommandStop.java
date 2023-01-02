public class CommandStop extends Command {

    public CommandStop() {
        super("stop", "/stop", 0, Security.OPERATOR);
    }

    @Override
    public boolean execute(UserConnection userConnection, String[] args) {
        if (!hasPermission(userConnection, SECURITY_LEVEL)) {
            notAllowed(userConnection);
            return false;
        }
        userConnection.getServer().shutdown();
        return true;
    }
}
