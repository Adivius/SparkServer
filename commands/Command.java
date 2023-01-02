public abstract class Command {
    public String USAGE;
    public String NAME;
    public int ARGS_LENGTH;
    public int SECURITY_LEVEL;


    public Command(String name, String usage, int argsLength, int securityLevel) {
        this.USAGE = usage;
        this.NAME = name;
        this.ARGS_LENGTH = argsLength;
        this.SECURITY_LEVEL = securityLevel;
    }

    public abstract boolean execute(UserConnection userConnection, String[] args);

    public boolean hasPermission(UserConnection userConnection, int minSecurityLevel) {
        return minSecurityLevel <= userConnection.getSecurityLevel();
    }

    public void notAllowed(UserConnection userConnection) {
        userConnection.sendLog("You don't have the permission to do that!");
    }
}
