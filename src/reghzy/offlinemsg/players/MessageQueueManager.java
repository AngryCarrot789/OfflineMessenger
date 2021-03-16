package reghzy.offlinemsg.players;

import reghzy.offlinemsg.OfflineMessenger;
import reghzy.offlinemsg.config.Config;
import reghzy.offlinemsg.logs.ChatLogger;
import reghzy.offlinemsg.logs.ChatMessageDelayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;

public final class MessageQueueManager {
    // a map where the key is a target player, and the value is a list of messages they have pending
    private static HashMap<String, ArrayList<PlayerMessage>> QueuedMessages;
    private static Config messageQueueConfig;

    private static final String ConfigQueueKey = "queued";
    private static ConfigurationSection ConfigQueueSection;

    // section contains all the senders
    public static ConfigurationSection GetTargetSection(String targetName){
        return ConfigQueueSection.getConfigurationSection(targetName);
    }

        // section contains all the senders
    public static ConfigurationSection GetOrCreateTargetSection(String targetName){
        ConfigurationSection section = ConfigQueueSection.getConfigurationSection(targetName);
        if (section == null) {
            section = ConfigQueueSection.createSection(targetName);
        }
        return section;
    }

    // returns the a specific sender's message
    public static String GetSenderMessage(String targetName, String senderName) {
        return GetTargetSection(targetName).getString(senderName);
    }

    public static ArrayList<PlayerMessage> GetTargetMessages(String targetName) {
        ConfigurationSection playerSection = GetOrCreateTargetSection(targetName);
        ArrayList<PlayerMessage> messages = new ArrayList<PlayerMessage>();
        for(String senderName : playerSection.getKeys(false)) {
            messages.add(new PlayerMessage(senderName, targetName, playerSection.getString(senderName)));
        }
        return messages;
    }

    public static void SetSenderMessage(String target, String sender, String message) {
        GetOrCreateTargetSection(target).set(sender, message);
    }

    public static void AddTargetMessage(String target, String sender, String message) {
        SetSenderMessage(target, sender, message);
    }

    public static boolean SetTarget(String targetName, Object value) {
        ConfigQueueSection.set(targetName, value);
        return QueuedMessages.remove(targetName) != null;
    }

    public static boolean ClearTargetMessages(String target) {
        return SetTarget(target, null);
    }

    public static void SetSenderInConfig(String target, String sender, Object value) {
        GetOrCreateTargetSection(target).set(sender,value);
    }

    public static void SetConfigMessages(ArrayList<PlayerMessage> messages) {
        for (PlayerMessage playerMessage : messages) {
            GetTargetSection(playerMessage.getTarget()).set(playerMessage.getSender(), playerMessage.getMessage());
        }
    }

    //private static ConfigurationSection GetSenderSection(String targetName, String senderName) {
    //    return GetTargetSection(targetName).getConfigurationSection(senderName);
    //}

    public static void Initialise(boolean isStartup) {
        messageQueueConfig = OfflineMessenger.getInstance().messageQueueConfig;
        ConfigQueueSection = messageQueueConfig.getConfigurationSection(ConfigQueueKey);
        QueuedMessages = new HashMap<String, ArrayList<PlayerMessage>>();
        LoadMessagesFromConfig(isStartup);
    }

    public static void LoadMessagesFromConfig(boolean addToConfig) {
        for (String targetName : ConfigQueueSection.getKeys(false)) {
            LoadMessagesFromPlayer(targetName, addToConfig);
        }
    }

    public static void LoadMessagesFromPlayer(String targetPlayer, boolean addToConfig) {
        ArrayList<PlayerMessage> messages = new ArrayList<PlayerMessage>(GetTargetMessages(targetPlayer));
        if (addToConfig) {
            AddMessagesToQueuedAndConfig(targetPlayer, messages);
        }
        else {
            AddMessagesToQueued(targetPlayer, messages);
        }
    }

    public static void OnPlayerJoined(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        ArrayList<PlayerMessage> message = GetOrCreateQueuedMessages(name);
        SendPlayerMessages(player, message);
    }

    private static void SendPlayerMessages(Player player, ArrayList<PlayerMessage> messages) {
        ChatLogger.LogConsole("§8" + player.getName() + " §6has pending messages!");
        ArrayList<String> playerMessages = new ArrayList<String>();
        if (messages.size() == 0) {
            playerMessages.add("§6------- §3You have no messages pending :( §6-------§r");
            return;
        }
        playerMessages.add("§6---------- §3You have §3" + messages.size() + " §3messages pending! §6----------§r");
        for (PlayerMessage message : messages) {
            playerMessages.add("§3" + "From §9" + message.getSender() + ": ");
            playerMessages.add("  " + ChatColor.translateAlternateColorCodes('&', message.getMessage()));
        }
        playerMessages.add("§6---- §3Do §7/om clear §3to delete all your messages! §6----§r");

        ChatMessageDelayer.SendDelayedMessages(player, playerMessages, OfflineMessenger.LoginSendQueuedMessageDelayTicks);
    }

    public static ArrayList<PlayerMessage> GetOrCreateQueuedMessages(String targetName) {
        return EnsureTargetMessageListExists(targetName);
    }

    public static ArrayList<String> GetAllQueuedPlayers() {
        return new ArrayList<String>(ConfigQueueSection.getKeys(false));
    }

    public static void AddMessagesToQueuedAndConfig(String target, ArrayList<PlayerMessage> newMessages) {
        ArrayList<PlayerMessage> messages = EnsureTargetMessageListExists(target);
        messages.addAll(newMessages);
        SetConfigMessages(messages);
    }

    public static void AddMessagesToQueued(String target, ArrayList<PlayerMessage> newMessages) {
        ArrayList<PlayerMessage> messages = EnsureTargetMessageListExists(target);
        for(PlayerMessage message : newMessages) {
            message.setMessage(ChatColor.translateAlternateColorCodes('&', message.getMessage()));
        }
        messages.addAll(newMessages);
    }

    public static void AddMessagesToQueuedAndConfig(String target, PlayerMessage message) {
        if (target == null) {
            return;
        }
        ArrayList<PlayerMessage> messages = EnsureTargetMessageListExists(target);
        for (PlayerMessage playerMessage : messages) {
            if (playerMessage.equals(message)) {
                messages.remove(playerMessage);
                break;
            }
        }
        messages.add(message);
        GetOrCreateTargetSection(target).set(message.getSender(), message.getMessage());

        Player targetPlayer = Bukkit.getPlayer(target);
        if (targetPlayer != null) {
            ChatMessageDelayer.SendDelayedMessage(targetPlayer, message, 10);
        }
    }

    public static void UpdateExistingMessageInConfig(PlayerMessage message) {
        EnsureTargetMessageListExists(message.getTarget());
        GetOrCreateTargetSection(message.getTarget()).set(message.getSender(), message.getMessage());
    }

    public static void RemoveTarget(String target) {
        ClearTargetMessages(target);
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

    public static ArrayList<PlayerMessage> EnsureTargetMessageListExists(String targetName) {
        ArrayList<PlayerMessage> messages = QueuedMessages.get(targetName);
        if (messages == null) {
            messages = new ArrayList<PlayerMessage>();
            QueuedMessages.put(targetName, messages);
            GetOrCreateTargetSection(targetName);
        }
        return messages;
    }

    public static void SaveConfig() {
        OfflineMessenger.getInstance().SaveMessageQueueConfig();
    }
}
