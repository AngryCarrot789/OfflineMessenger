package reghzy.offlinemsg.logs;

import reghzy.offlinemsg.OfflineMessenger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatLogger {
    private CommandSender sender;

    public ChatLogger(CommandSender sender) {
        setSender(sender);
    }

    public void setSender(CommandSender sender) {
        this.sender = sender;
    }

    public void LogPlayer(String text) {
        if (sender != null) {
            sender.sendMessage(text);
        }
    }

    public void LogAny(String text) {
        if (sender == null) {
            LogConsole(text);
        }
        else {
            LogPlayer(text);
        }
    }

    public void LogError(String text) {
        LogAny(ChatColor.RED + text);
    }

    public static void LogConsole(String text) {
        Bukkit.getConsoleSender().sendMessage(text);
    }

    public static void LogPlugin(String text){
        LogConsole(OfflineMessenger.PREFIX + " " + ChatColor.GOLD + text);
    }
}
