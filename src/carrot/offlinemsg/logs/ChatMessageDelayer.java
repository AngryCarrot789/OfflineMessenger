package carrot.offlinemsg.logs;

import carrot.offlinemsg.OfflineMessenger;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
}
