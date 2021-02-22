package carrot.offlinemsg.players;

import carrot.offlinemsg.OfflineMessenger;
import carrot.offlinemsg.config.Config;
import carrot.offlinemsg.logs.ChatLogger;
import carrot.offlinemsg.logs.ChatMessageDelayer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public final class MessageQueueManager {
    private static HashMap<String, ArrayList<PlayerMessage>> QueuedMessages;
    private static Config messageQueueConfig;

    private static final String ConfigQueueKey = "queued";
    private static final String ConfigQueueKeySection = "queued.";
    private static ConfigurationSection ConfigQueueSection;

    public static void Initialise() {
        messageQueueConfig = OfflineMessenger.getInstance().messageQueueConfig;
        ConfigQueueSection = messageQueueConfig.getConfigurationSection(ConfigQueueKey);
        QueuedMessages = new HashMap<String, ArrayList<PlayerMessage>>();
        LoadMessagesFromConfig();
    }

    public static void LoadMessagesFromConfig() {
        for (String targetKey : ConfigQueueSection.getKeys(false)) {
            ChatLogger.LogPlugin("Found queued message for " + ChatColor.GREEN + targetKey);
            LoadMessagesFromPlayer(targetKey);
        }
    }

    public static void LoadMessagesFromPlayer(String targetPlayer) {
        ConfigurationSection senderSection = ConfigQueueSection.getConfigurationSection(targetPlayer);
        if (senderSection == null) {
            return;
        }
        ArrayList<PlayerMessage> messages = new ArrayList<PlayerMessage>();
        for (String senderName : senderSection.getKeys(false)) {
            if (senderName == null) {
                return;
            }
            String message = senderSection.getString(senderName);
            if (message == null) {
                return;
            }
            ChatLogger.LogPlugin("Found queued message for " + targetPlayer + " from " + senderName + ": " + ChatColor.RESET + message);
            messages.add(new PlayerMessage(senderName, targetPlayer, message));
        }
        AddMessagesToQueuedAndConfig(targetPlayer, messages);
    }

    public static void OnPlayerJoined(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        ArrayList<PlayerMessage> message = GetQueuedMessages(name);
        if (message != null) {
            SendPlayerMessages(player, message);
        }
    }

    private static void SendPlayerMessages(Player player, ArrayList<PlayerMessage> messages) {
        ChatLogger.LogConsole("§8" + player.getName() + " §6has pending messages!");
        ArrayList<String> playerMessages = new ArrayList<String>();
        playerMessages.add("§6------- §3You have §7" + messages.size() + " §3messages pending! §6-------§r");
        for (PlayerMessage message : messages) {
            playerMessages.add("§3" + "From §9" + message.getSender() + ": ");
            playerMessages.add("  " + message.getSender());
        }
        playerMessages.add("§6---- §3Do §7/om clear §3to delete all your messages! §6----§r");

        ChatMessageDelayer.SendDelayedMessages(player, playerMessages, 100);
    }

    public static ArrayList<PlayerMessage> GetQueuedMessages(String name) {
        return QueuedMessages.get(name);
    }

    public static void AddMessagesToQueuedAndConfig(String target, ArrayList<PlayerMessage> newMessages) {
        ArrayList<PlayerMessage> messages = QueuedMessages.get(target);
        if (messages == null) {
            messages = new ArrayList<PlayerMessage>();
            QueuedMessages.put(target, messages);
        }
        messages.addAll(newMessages);
        for (PlayerMessage playerMessage : messages) {
            ConfigQueueSection.getConfigurationSection(playerMessage.getTarget()).set(playerMessage.getSender(), playerMessage.getMessages());
        }
    }

    public static void AddMessagesToQueuedAndConfig(String target, PlayerMessage message) {
        ArrayList<PlayerMessage> messages = QueuedMessages.get(target);
        if (messages == null) {
            messages = new ArrayList<PlayerMessage>();
            QueuedMessages.put(target, messages);
            ConfigurationSection playerSection = ConfigQueueSection.getConfigurationSection(message.getTarget());
            if (playerSection != null) {
                playerSection.set(message.getSender(), message.getMessages());
            }
            else {
                ConfigQueueSection.set(message.getTarget(), message.getSender());
            }
        }
        messages.add(message);
        ConfigQueueSection.getConfigurationSection(message.getTarget()).set(message.getSender(), message.getMessages());
    }

    public static void UpdateExistingMessageInConfig(PlayerMessage message) {
        ArrayList<PlayerMessage> messages = QueuedMessages.get(message.getTarget());
        if (messages == null) {
            messages = new ArrayList<PlayerMessage>();
            QueuedMessages.put(message.getTarget(), messages);
            ConfigurationSection playerSection = ConfigQueueSection.getConfigurationSection(message.getTarget());
            if (playerSection != null) {
                playerSection.set(message.getSender(), message.getMessages());
            }
            else {
                ConfigQueueSection.set(message.getTarget(), message.getSender());
            }
        }
        //for(PlayerMessage actualMessages : messages){
        //    if (actualMessages.getTarget().equals(message.getTarget())){
        //        if (actualMessages.getSender().equals(message.getSender())){
        //            actualMessages.setMessages(message.getMessages());
        //            return;
        //        }
        //    }
        //}
        ConfigQueueSection.getConfigurationSection(message.getTarget()).set(message.getSender(), message.getMessages());
    }

    public static void RemoveTarget(String target) {
        if (QueuedMessages.remove(target) != null) {
            ConfigQueueSection.set(target, null);
        }
    }

    public static void RemoveMessage(String target, String sender) {
        for (PlayerMessage message : QueuedMessages.get(target)) {
            if (message.getSender().equals(sender)) {
                if (QueuedMessages.remove(target) != null) {
                    ConfigQueueSection.set(target, null);

                }
            }
        }
    }

    public static void RemoveTarget(PlayerMessage message) {
        RemoveTarget(message.getTarget());
    }
}
