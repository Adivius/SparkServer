public class Message {

    public String MESSAGE;
    public String SENDER;
    public String RECIPIENT;
    public long TIMESTAMP;


    public Message(String message, String sender, String recipient, long timestamp) {
        MESSAGE = message;
        SENDER = sender.toLowerCase();
        RECIPIENT = recipient.toLowerCase();
        TIMESTAMP = timestamp;
    }

    public String printMessage() {
        return "[" + MESSAGE + ":" + SENDER + ":" + RECIPIENT + ":" + TIMESTAMP + "]";
    }
}
