public class User {

    public String NAME;
    public String PW_HASH;
    public int LEVEL;
    public int BANNED;

    public User(String name, String pwHash, int level, int banned) {
        PW_HASH = pwHash;
        NAME = name.toLowerCase();
        LEVEL = level;
        BANNED = banned;
    }

    public User(String name, String pwHash) {
        PW_HASH = pwHash;
        NAME = name.toLowerCase();
        LEVEL = Security.MEMBER;
        BANNED = 0;
    }

    public String printUser() {
        return "[" + NAME + ":" + PW_HASH + ":" + LEVEL + ":" + BANNED + "]";
    }


}
