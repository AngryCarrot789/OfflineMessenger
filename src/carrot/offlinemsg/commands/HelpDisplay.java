package carrot.offlinemsg.commands;

import carrot.offlinemsg.PermissionsHelper;
import carrot.offlinemsg.logs.ChatLogger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
        if (ignorePermissions || PermissionsHelper.HasPermission(player, "om.showPlayer")) {
            LogCMD("/om show [player]", "Shows your (or a players) messages");
        }
        else {
            LogCMD("/om show", "Shows all the messages for you");
        }
        if (ignorePermissions || PermissionsHelper.HasPermission(player, "om.sendPlayer")) {
            LogCMD("/om send <player> <msg>", "Queues 1 message for a player");
            LogRaw(ChatColor.YELLOW + "(supports colours, using the & character)");
            LogRaw(ChatColor.YELLOW + "(Use underscores not of spaces, like_this_ok)");
            LogRaw(ChatColor.RED + "(you can only send 1 message per sender, sending another will override your old one!)");
        }
        if (ignorePermissions || PermissionsHelper.HasPermission(player, "om.clearPlayer")) {
            LogCMD("/om clear [player]", "Clears your (or a players) messages");
        }
        else {
            LogCMD("/om clear", "Clears all your messages");
        }
        if (ignorePermissions || PermissionsHelper.HasPermission(player, "om.reload")) {
            LogCMD("/om reload", "Re-load all messages from the config");
        }
        LogTtl("--------------------------------------------------------");
    }
}
