package carrot.offlinemsg.players;

import java.util.ArrayList;

public class PlayerMessage {
    private String sender;
    private String target;
    private ArrayList<String> messages;

    public PlayerMessage(String sender, String target, ArrayList<String> messages) {
        this.sender = sender;
        this.target = target;
        this.messages = messages;
    }

    public PlayerMessage(String sender, String target) {
        this.sender = sender;
        this.target = target;
        this.messages = new ArrayList<String>(1);
    }

    public void AddLine(String message){
        this.messages.add(message);
    }

    public boolean RemoveLine(int line){
        if (line > 0) {
            return RemoveIndex(line - 1);
        }
        return false;
    }

    public boolean RemoveIndex(int index) {
        if (index >= this.messages.size()) {
            this.messages.remove(index);
            return true;
        }
        return false;
    }

    public String getSender() {
        return this.sender;
    }

    public String getTarget() {
        return this.target;
    }

    public void setSender(String sender){
        this.sender = sender;
    }

    public void setTarget(String target){
        this.target = target;
    }

    public ArrayList<String> getMessages() {
        return this.messages;
    }

    @Override
    public int hashCode() {
        return sender.hashCode() + target.hashCode() + messages.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        PlayerMessage message = (PlayerMessage) obj;
        return message.sender.equals(sender) &&
                message.target.equals(target) &&
                message.messages.equals(messages);
    }
}
