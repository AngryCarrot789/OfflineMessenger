package carrot.offlinemsg.logs;

import carrot.offlinemsg.OfflineMessenger;
import carrot.offlinemsg.players.PlayerMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * A class for sending delayed messages to a player
 */
public class ChatMessageDelayer {
    public static void SendDelayedMessage(final Player player, final String message, long delayedTicks) {
        BukkitRunnable delayedMessage = new BukkitRunnable() {
            @Override
            public void run() {
                player.sendMessage(message);
            }
        };
        delayedMessage.runTaskLater(OfflineMessenger.getInstance(), delayedTicks);
    }

    public static void SendDelayedMessage(final Player player, final PlayerMessage message, long delayedTicks) {
        ArrayList<String> playerMessages = new ArrayList<String>();
        playerMessages.add("§6-------------- §3You have a new message! §6--------------§r");
        playerMessages.add("§3" + "From §9" + message.getSender() + ": ");
        playerMessages.add("  " + ChatColor.translateAlternateColorCodes('&', message.getMessage()));
        playerMessages.add("§6---- §3Do §7/om clear §3to delete all your messages! §6----§r");
        ChatMessageDelayer.SendDelayedMessages(player, playerMessages, delayedTicks);
    }

    public static void SendDelayedMessages(final Player player, final String[] messages, long delayedTicks) {
        BukkitRunnable delayedMessage = new BukkitRunnable() {
            @Override
            public void run() {
                for (String line : messages) {
                    player.sendMessage(line);
                }
            }
        };
        delayedMessage.runTaskLater(OfflineMessenger.getInstance(), delayedTicks);
    }

    public static void SendDelayedMessagesIndented(final Player player, final String[] messages, long delayedTicks) {
        BukkitRunnable delayedMessage = new BukkitRunnable() {
            @Override
            public void run() {
                for (String line : messages) {
                    player.sendMessage("  " + line);
                }
            }
        };
        delayedMessage.runTaskLater(OfflineMessenger.getInstance(), delayedTicks);
    }

    public static void SendDelayedMessages(final Player player, final ArrayList<String> messages, long delayedTicks) {
        SendDelayedMessages(player, messages.toArray(new String[0]), delayedTicks);
    }
}
