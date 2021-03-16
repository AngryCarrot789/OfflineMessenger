package reghzy.offlinemsg.config;

import reghzy.offlinemsg.OfflineMessenger;
import reghzy.offlinemsg.logs.ChatLogger;
import reghzy.offlinemsg.logs.format.ChatFormatting;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Config extends YamlConfiguration {
    private File file;

    public Config(Plugin plugin, String name) throws IOException, InvalidConfigurationException {
        this.file = new File(plugin.getDataFolder(), name);
        if (!this.file.exists())
            ChatLogger.LogPlugin(ChatFormatting.Apostrophise(name) + " doesn't exist. Creating...");
            file.getParentFile().mkdirs();
            if (plugin.getResource(name) != null) {
                plugin.saveResource(name, false);
                ChatLogger.LogPlugin("Default config " + ChatFormatting.Apostrophise(name) + " found and created!");
            }
            else {
                this.file.mkdirs();
                this.file.createNewFile();
                ChatLogger.LogPlugin("Default config " + ChatFormatting.Apostrophise(name) + " wasn't found. Creating it anyway!");
            }
        load(this.file);
    }

    public void SaveConfig() throws IOException {
        save(this.file);
        ChatLogger.LogPlugin("Saved config file " + ChatFormatting.Apostrophise(file.getName()));
    }

    public boolean TrySave() {
        if (file == null){
            ChatLogger.LogPlugin("Config file handle was null.... somehow");
            return false;
        }
        try {
            ChatLogger.LogPlugin("Trying to save config file " + ChatFormatting.Apostrophise(file.getName()) + "...");
            SaveConfig();
            ChatLogger.LogPlugin("Success!");
            return true;
        }
        catch (IOException ex) {
            ChatLogger.LogPlugin("Failed to save config " + ChatFormatting.Apostrophise(file.getName()) + "...");
            OfflineMessenger.getInstance().getLogger().log(Level.SEVERE, "Couldn't save " + this.file.getName() + " to disk", ex);
            ex.printStackTrace();
            return false;
        }
    }
}
