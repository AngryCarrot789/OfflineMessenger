package carrot.offlinemsg.players;

import carrot.offlinemsg.OfflineMessenger;
import carrot.offlinemsg.config.Config;
import carrot.offlinemsg.logs.ChatLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public final class MessageQueueManager {
    /*
        String is the target player...
        PlayerMessage is the message ;))
     */
    private static HashMap<String, PlayerMessage> QueuedMessages;
    private static Config messageQueueConfig;

    public static void Initialise() {
        messageQueueConfig = OfflineMessenger.getInstance().messageQueueConfig;
        QueuedMessages = new HashMap<String, PlayerMessage>();
        LoadMessagesFromConfig();
    }

    public static void LoadMessagesFromConfig() {
        for(String playerKey : messageQueueConfig.getConfigurationSection("").getKeys(false)){
            LoadMessagesFromPlayer(playerKey);
        }
    }

    public static void LoadMessagesFromPlayer(String playerName) {
        List<String> messages = messageQueueConfig.getStringList(playerName);
        PlayerMessage message =
                new PlayerMessage(
                        MessageParser.GetSender(messages),
                        playerName,
                        new ArrayList<String>(MessageParser.GetMessages(messages)));
        AddMessage(playerName, message);
    }

    public static void OnPlayerJoined(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        PlayerMessage message = GetQueuedMessage(name);
        if (message != null){
            SendPlayerMessages(player, message);
        }
    }

    private static void SendPlayerMessages(Player player, PlayerMessage message) {
        if (message.getMessages().size() > 0) {
            ChatLogger.LogConsole("§8" + player.getName() + " §6has pending messages!");
            player.sendMessage("§6------- §3You have §7" + message.getMessages().size() + " §3messages pending! §6-------§r");

            player.sendMessage("§3" + "From §9" + message.getSender() + ": ");
            for(String line : message.getMessages()){
                player.sendMessage("  " + line);
            }

            player.sendMessage("§6---- §3Do §7/om clear §3to delete all your messages! §6----§r");
        }
    }

    public static PlayerMessage GetQueuedMessage(String name){
        return QueuedMessages.get(name);
    }

    public static void AddMessage(String target, PlayerMessage message) {
        QueuedMessages.put(target, message);
        OfflineMessenger.getInstance().messageQueueConfig.set(target, message.getMessages());
        OfflineMessenger.getInstance().SaveMessageQueueConfig();
    }

    public static void RemoveTarget(String target) {
        if (QueuedMessages.remove(target) != null) {
            OfflineMessenger.getInstance().SaveMessageQueueConfig();
            OfflineMessenger.getInstance().messageQueueConfig.set(target, null);
        }
    }

    public static void RemoveTarget(PlayerMessage message) {
        RemoveTarget(message.getTarget());
    }

    private void UpdateConfigMessages(){
        Config config = OfflineMessenger.getInstance().messageQueueConfig;
        for(Map.Entry<String, PlayerMessage> pair : QueuedMessages.entrySet()) {
            PlayerMessage message = pair.getValue();
            if (message != null) {
                config.set(pair.getKey(), message.getMessages());
            }
        }
    }

    public Collection<PlayerMessage> GetMessages() {
        return QueuedMessages.values();
    }
}
