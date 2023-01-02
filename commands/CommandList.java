public class CommandList extends Command {
    public CommandList() {
        super("list", "/list", 0, Security.MEMBER);
    }

    @Override
    public boolean execute(UserConnection userConnection, String[] args) {
        if (!hasPermission(userConnection, SECURITY_LEVEL)) {
            notAllowed(userConnection);
            return false;
        }
        userConnection.sendLog(userConnection.getServer().getUserNames());
        return true;
    }
}
