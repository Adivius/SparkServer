import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class Security {

    public static final int VISITOR = 0, MEMBER = 1, ADMIN = 2, OPERATOR = 3;
    public static final String[] FORBIDDEN_NAMES = {"system", "server", "operator", "admin", "unknown", "console", "general"};
    public static final int NAME_MAX_LENGTH = 12;
    public static final String STANDARD_RECIPIENT = "general";

    public static boolean hasPermission(UserConnection userConnection, int minSecurityLevel) {
        return userConnection.getUser().LEVEL >= minSecurityLevel;
    }

    public static boolean isInvalidInt(String str) {
        try {
            Integer.parseInt(str);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static boolean nameDenied(String name) {
        boolean out = false;
        for (String forbiddenName : FORBIDDEN_NAMES) {
            if (name.toLowerCase().contains(forbiddenName)) {
                out = true;
                break;
            }
        }
        return out;
    }

    public static boolean isNameAllowed(String name) {
        if (name.length() > NAME_MAX_LENGTH) {
            return false;
        }
        for (String forbiddenName : FORBIDDEN_NAMES) {
            if (name.toLowerCase().contains(forbiddenName)) {
                return false;
            }
        }
        return StandardCharsets.US_ASCII.newEncoder().canEncode(name);
    }


    private static byte[] digest(byte[] input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return md.digest(input);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String hashMd5(String input) {
        return bytesToHex(digest(input.getBytes(StandardCharsets.UTF_8)));
    }
}
