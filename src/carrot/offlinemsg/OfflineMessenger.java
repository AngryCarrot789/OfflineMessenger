package carrot.offlinemsg;

import carrot.offlinemsg.commands.OfflineMessengerCommandHandler;
import carrot.offlinemsg.config.Config;
import carrot.offlinemsg.listeners.PlayerJoinListener;
import carrot.offlinemsg.logs.ChatLogger;
import carrot.offlinemsg.players.MessageQueueManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class OfflineMessenger extends JavaPlugin {
    private static OfflineMessenger instance;

    public Config messageQueueConfig;
    public Config mainConfig;
    public ChatLogger logger;
    public PlayerJoinListener playerJoinListener;
    public static int LoginSendQueuedMessageDelayTicks = 100;

    public static final String PREFIX = "§6[§7OfflineMessenger§6]§r";

    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("PermissionsEx") == null) {
            ChatLogger.LogPlugin("PermissionsEx is not avaliable. Cannot enable OfflineMessenger");
            this.setEnabled(false);
            return;
        }
        else {
            ChatLogger.LogPlugin("PermissionsEx Avaliable!");
        }
        instance = this;
        logger = new ChatLogger(null);
        ChatLogger.LogPlugin("Enabling plugin...");

        ReloadAllConfigs(true);

        ChatLogger.LogPlugin("Initialising player join listener...");
        playerJoinListener = new PlayerJoinListener(this);
        ChatLogger.LogPlugin("Success!");

        ChatLogger.LogPlugin("Initialising command executors...");
        this.getCommand("offlinemessenger").setExecutor(new OfflineMessengerCommandHandler());
        ChatLogger.LogPlugin("Success!");

        ChatLogger.LogPlugin("Enabled OfflineMessenger :)");
    }

    public void onDisable() {
        if (this.isEnabled()) {
            ChatLogger.LogPlugin("Saving configs... :(");
            SaveMessageQueueConfig();
            SaveMainConfig();
            ChatLogger.LogPlugin("Disabled OfflineMessenger :(");
        }
        else {
            ChatLogger.LogPlugin("Disabled OfflineMessenger after issues occoured in " + ChatColor.GREEN + "onEnable()");
        }
    }

    public void ReloadAllConfigs(boolean isStartup) {
        ReloadMainConfig();
        ReloadMessageQueueConfig();
        ChatLogger.LogPlugin("Initialising message queue...");
        MessageQueueManager.Initialise(isStartup);
        ChatLogger.LogPlugin("Success!");
    }

    public void ReloadMainConfig(){
        ChatLogger.LogPlugin("Loading main config...");
        if (TryLoadMainConfig())
            ChatLogger.LogPlugin("Success!");
        else
            ChatLogger.LogPlugin("Failed!");
    }

    public void ReloadMessageQueueConfig(){
        ChatLogger.LogPlugin("Loading messages queue config...");
        if (TryLoadMessageQueueConfig())
            ChatLogger.LogPlugin("Success!");
        else
            ChatLogger.LogPlugin("Failed!");
    }

    public void SaveMainConfig(){
        ChatLogger.LogPlugin("Saving main config...");
        if (mainConfig.TrySave())
            ChatLogger.LogPlugin("Success!");
        else
            ChatLogger.LogPlugin("Failed!");
    }

    public void SaveMessageQueueConfig(){
        ChatLogger.LogPlugin("Saving messages queue config...");
        if (messageQueueConfig.TrySave())
            ChatLogger.LogPlugin("Success!");
        else
            ChatLogger.LogPlugin("Failed!");
    }

    private boolean TryLoadMainConfig() {
        try {
            mainConfig = new Config(this, "config.yml");
            LoginSendQueuedMessageDelayTicks = mainConfig.getInt("loginSendQueuedMessageDelayTicks");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private boolean TryLoadMessageQueueConfig() {
        try {
            messageQueueConfig = new Config(this, "messageQueue.yml");
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static OfflineMessenger getInstance() {
        return instance;
    }
}
