public class CommandKickAll extends Command {

    public CommandKickAll() {
        super("kickall", "/kickall", 0, Security.OPERATOR);
    }

    @Override
    public boolean execute(User user, String[] args) {
        if (!hasPermission(user, SECURITY_LEVEL)) {
            notAllowed(user);
            return false;
        }
        try {
            for (String id : user.getServer().getUsers().keySet()) {
                if (!(id.equals(user.getUserId()))) {
                    user.getServer().removeUserById(id, "Kicked by Admin");
                }
            }
            user.sendLog("Kicked all user!");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
