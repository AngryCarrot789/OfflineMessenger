package reghzy.offlinemsg.players;

public class PlayerMessage {
    private String sender;
    private String target;
    private String messages;

    public PlayerMessage(String sender, String target, String messages) {
        this.sender = sender;
        this.target = target;
        this.messages = messages;
    }

    public PlayerMessage(String sender, String target) {
        this.sender = sender;
        this.target = target;
        this.messages = "";
    }

    public String getMessage() {
        return this.messages;
    }

    public void setMessage(String messages) {
        this.messages = messages;
    }

    public String getSender() {
        return this.sender;
    }

    public String getTarget() {
        return this.target;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public int hashCode() {
        return sender.hashCode() + target.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        PlayerMessage message = (PlayerMessage) obj;
        return message.sender.equals(sender) &&
                message.target.equals(target);
    }
}
