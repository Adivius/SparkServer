public class PacketConnect extends Packet {

    public String USERNAME;

    public String PW_HASH;

    public PacketConnect(String userName, String pw_hash) {
        super(PacketIds.CONNECT);
        this.USERNAME = userName;
        this.PW_HASH = pw_hash;
    }

    public PacketConnect(String[] str) {
        super(PacketIds.CONNECT);
        if (str.length >= 3) {
            this.USERNAME = str[1];
            this.PW_HASH = str[2];

        } else if (str.length == 2) {
            this.USERNAME = str[1];
            this.PW_HASH = null;
        } else {
            this.USERNAME = null;
            this.PW_HASH = null;
        }
    }

    @Override
    public String encode() {
        return PacketIds.CONNECT + PacketIds.SEPARATOR + USERNAME + PacketIds.SEPARATOR + PW_HASH;
    }
}
