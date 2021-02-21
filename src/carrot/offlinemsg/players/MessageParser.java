package carrot.offlinemsg.players;

import java.util.ArrayList;
import java.util.List;

public class MessageParser {
    public static String GetSender(List<String> list) {
        if (list == null)
            return null;
        return list.get(0);
    }

    public static List<String> GetMessages(List<String> list){
        List<String> dupe = new ArrayList<String>(list);
        if (dupe.size() > 0)
            dupe.remove(0);
        return dupe;
    }

    public static void RemoveMessage(ArrayList<String> messages, int index){
        if (messages.size() > 1) {
            messages.remove(index + 1);
        }
    }

    public static void SetSender(ArrayList<String> messages, String name){
        messages.set(0, name);
    }
}
