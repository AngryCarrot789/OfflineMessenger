package carrot.offlinemsg.listeners;

import carrot.offlinemsg.OfflineMessenger;
import carrot.offlinemsg.players.MessageQueueManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    public PlayerJoinListener(OfflineMessenger messenger) {
        Bukkit.getPluginManager().registerEvents(this, messenger);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        MessageQueueManager.OnPlayerJoined(event);
    }
}
