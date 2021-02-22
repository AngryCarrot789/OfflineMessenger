package carrot.offlinemsg.commands;

import carrot.offlinemsg.PermissionsHelper;
import carrot.offlinemsg.logs.ChatLogger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public final class HelpDisplay {
    public static ChatLogger logger;

    private static void LogRaw(String info) {
        logger.LogAny(info);
    }

    private static void LogTtl(String info) {
        logger.LogAny(ChatColor.AQUA + info);
    }

    private static void LogCMD(String name, String description) {
        logger.LogAny(ChatColor.GREEN + name + " - " + ChatColor.DARK_GREEN + description);
    }

    public static void DisplayHelp(Player player, boolean ignorePermissions) {
        if (logger == null) {
            logger = new ChatLogger(player);
        }
        else {
            logger.setSender(player);
        }
        if (!ignorePermissions && player == null) {
            LogRaw(ChatColor.RED + "Player was null but ignore permissions wasnt true... Bug!!!! :@");
            return;
        }

        LogTtl("-----------< OfflineMessenger Help Page 1 >-----------");
        LogCMD("/om", "The main command");
        LogCMD("/om help", "Displays commands for OfflineMessenger");
        if (ignorePermissions || PermissionsHelper.HasPermission(player, "om.clearPlayer")) {
            LogCMD("/om clear <player>", "Clears all your messages, or for a given player");
        }
        else {
            LogCMD("/om clear", "Clears all your messages");
        }
        if (ignorePermissions || PermissionsHelper.HasPermission(player, "om.show")) {
            LogCMD("/om show <player>", "Shows all the messages for a player");
        }
        if (ignorePermissions || PermissionsHelper.HasPermission(player, "om.add")) {
            LogCMD("/om add <player> <msg>", "Queues a single line message for a player");
        }
        if (ignorePermissions || PermissionsHelper.HasPermission(player, "om.remove")) {
            LogCMD("/om remove <player> <lineNumber>", "Removes a line from a player");
        }
        LogTtl("--------------------------------------------------------");
    }
}
