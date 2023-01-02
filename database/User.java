public class User {

    public String NAME;
    public String PW_HASH;
    public int LEVEL;

    public User(String name, String pwHash, int level) {
        PW_HASH = pwHash;
        NAME = name.toLowerCase();
        LEVEL = level;
    }

    public User(String name, String pwHash) {
        PW_HASH = pwHash;
        NAME = name.toLowerCase();
        LEVEL = Security.MEMBER;
    }

    public String printUser() {
        return "[" + NAME + ":" + PW_HASH + ":" + LEVEL + "]";
    }


}
