public class PacketMessage extends Packet {

    public String MESSAGE;
    public String SENDER;
    public long TIMESTAMP;
    public String RECIPIENT;

    public PacketMessage(String message, String sender, long timestamp, String recipient) {
        super(PacketIds.MESSAGE);
        this.MESSAGE = message;
        this.SENDER = sender;
        this.TIMESTAMP = timestamp;
        this.RECIPIENT = recipient;
    }


    public PacketMessage(String[] str) {
        super(PacketIds.MESSAGE);
        if (str.length >= 5) {
            this.MESSAGE = str[1];
            this.SENDER = str[2];
            this.TIMESTAMP = Long.parseLong(str[3]);
            this.RECIPIENT = str[4];
        } else if (str.length == 4) {
            this.MESSAGE = str[1];
            this.SENDER = str[2];
            this.TIMESTAMP = Long.parseLong(str[3]);
            this.RECIPIENT = Security.STANDARD_RECIPIENT;
        } else if (str.length == 3) {
            this.MESSAGE = str[1];
            this.SENDER = str[2];
            this.TIMESTAMP = 0;
            this.RECIPIENT = Security.STANDARD_RECIPIENT;
        } else if (str.length == 2) {
            this.MESSAGE = str[1];
            this.SENDER = null;
            this.TIMESTAMP = 0;
            this.RECIPIENT = Security.STANDARD_RECIPIENT;
        } else {
            this.MESSAGE = null;
            this.SENDER = null;
            this.TIMESTAMP = 0;
            this.RECIPIENT = Security.STANDARD_RECIPIENT;
        }
    }

    @Override
    public String encode() {
        return PacketIds.MESSAGE + PacketIds.SEPARATOR + MESSAGE + PacketIds.SEPARATOR + SENDER + PacketIds.SEPARATOR + TIMESTAMP + PacketIds.SEPARATOR + RECIPIENT;
    }

    public Message getMessage() {
        return new Message(MESSAGE, SENDER, RECIPIENT, TIMESTAMP);
    }
}
