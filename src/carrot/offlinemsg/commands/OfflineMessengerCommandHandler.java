package carrot.offlinemsg.commands;

import carrot.offlinemsg.OfflineMessenger;
import carrot.offlinemsg.PermissionsHelper;
import carrot.offlinemsg.logs.ChatLogger;
import carrot.offlinemsg.players.MessageQueueManager;
import carrot.offlinemsg.players.PlayerMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class OfflineMessengerCommandHandler implements CommandExecutor {
    private final ChatLogger logger = new ChatLogger(null);
    public boolean Check(String checkEqual, String toThis) {
        return checkEqual.equalsIgnoreCase(toThis);
    }

    public void ExecuteCommand(String command, String[] args, CommandSender sender) {
        logger.setSender(sender);
        boolean isPlayer = false;
        Player playerSender = null;
        if (sender instanceof Player) {
            playerSender = (Player) sender;
            isPlayer = true;
        }

        if (Check("help", command)) {
            if (isPlayer) {
                HelpDisplay.DisplayHelp(playerSender, false);
            }
            else {
                HelpDisplay.DisplayHelp(null, true);
            }
        }
        else if (Check("clear", command)) {
            ParsedValue<String> parsedPlayer = ArgumentsParser.ParseString(args, 0);
            if (isPlayer) {
                if (PermissionsHelper.HasPermissionOrOp(playerSender, "om.clearPlayer")) {
                    if (parsedPlayer.failed()) {
                        MessageQueueManager.RemoveTarget(playerSender.getName());
                        MessageQueueManager.SaveConfig();
                        logger.LogAny(ChatColor.GREEN + "Cleared your messages!");
                        return;
                    }
                    MessageQueueManager.RemoveTarget(parsedPlayer.getValue());
                    MessageQueueManager.SaveConfig();
                    logger.LogAny(ChatColor.GREEN + "Cleared their messages!");
                }
                else {
                    MessageQueueManager.RemoveTarget(playerSender.getName());
                    MessageQueueManager.SaveConfig();
                    logger.LogAny(ChatColor.GREEN + "Cleared your messages!");
                }
            }
            else {
                if (parsedPlayer.failed()) {
                    logger.LogAny(ChatColor.RED + "Error with the player name");
                    return;
                }
                MessageQueueManager.RemoveTarget(parsedPlayer.getValue());
                MessageQueueManager.SaveConfig();
                logger.LogAny(ChatColor.GOLD + "Cleared " + ChatColor.GREEN + parsedPlayer.getValue() + ChatColor.GOLD + " messages!");
            }
        }
        else if (Check("send", command)) {
            if (isPlayer) {
                if (!PermissionsHelper.HasPermissionOrOp(playerSender, "om.sendPlayer")) {
                    logger.LogAny(ChatColor.RED + "You dont have permission for this command");
                    return;
                }
            }
            ParsedValue<String> parsedPlayer = ArgumentsParser.ParseString(args, 0);
            ParsedValue<String> parsedMessage = ArgumentsParser.ParseString(args, 1);
            if (parsedPlayer.failed()) {
                logger.LogAny("Error with the player's name");
                return;
            }
            if (parsedMessage.failed()) {
                logger.LogAny("Error with the message to be queued");
                return;
            }
            MessageQueueManager.GetOrCreateQueuedMessages(parsedPlayer.getValue());
            String messageTranslated = parsedMessage.getValue().replace('_', ' ');
            PlayerMessage message = new PlayerMessage(sender.getName(), parsedPlayer.getValue(), messageTranslated);
            MessageQueueManager.AddMessagesToQueuedAndConfig(parsedPlayer.getValue(), message);
            logger.LogAny(ChatColor.GOLD + "Added: " + ChatColor.GREEN + message.getMessage());
            logger.LogAny(ChatColor.GOLD + "(Preview: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message.getMessage()) + ChatColor.GOLD + ")");
            MessageQueueManager.SaveConfig();
        }
        else if (Check("show", command)) {
            ParsedValue<String> parsedTarget = ArgumentsParser.ParseString(args, 0);
            if (parsedTarget.failed()) {
                ArrayList<PlayerMessage> messages = MessageQueueManager.GetOrCreateQueuedMessages(sender.getName());
                if (messages.size() == 0) {
                    sender.sendMessage("§6------- §3You have no messages pending :( §6-------§r");
                    return;
                }
                sender.sendMessage("§6---------- §3You have §a" + messages.size() + " §3messages pending! §6----------§r");
                for (PlayerMessage message : messages) {
                    sender.sendMessage("§3" + "From §9" + message.getSender() + ": ");
                    sender.sendMessage("  " + ChatColor.translateAlternateColorCodes('&', message.getMessage()));
                }
                sender.sendMessage("§6---- §3Do §7/om clear §3to delete all your messages! §6----§r");
            }
            else {
                if (!isPlayer || PermissionsHelper.HasPermissionOrOp(playerSender, "om.showPlayer")) {
                    ArrayList<PlayerMessage> messages = MessageQueueManager.GetOrCreateQueuedMessages(parsedTarget.getValue());
                    if (messages.size() == 0) {
                        sender.sendMessage("§6------- §3They have no messages pending :( §6-------§r");
                        return;
                    }
                    sender.sendMessage("§6---- §b" + parsedTarget.getValue() + " §3has §a" + messages.size() + " §3messages pending! §6----§r");
                    for (PlayerMessage message : messages) {
                        sender.sendMessage("§3" + "From §9" + message.getSender() + ": ");
                        sender.sendMessage("-  " + ChatColor.translateAlternateColorCodes('&', message.getMessage()));
                    }
                    sender.sendMessage("§6---- §3Do §7/om clear <name> §3to clear their messages! §6----§r");
                }
                else {
                    ArrayList<PlayerMessage> messages = MessageQueueManager.GetOrCreateQueuedMessages(sender.getName());
                    if (messages.size() == 0) {
                        sender.sendMessage("§6------- §3You have no messages pending :( §6-------§r");
                        return;
                    }
                    sender.sendMessage("§6---------- §3You have §a" + messages.size() + " §3messages pending! §6----------§r");
                    for (PlayerMessage message : messages) {
                        sender.sendMessage("§3" + "From §9" + message.getSender() + ": ");
                        sender.sendMessage("  " + ChatColor.translateAlternateColorCodes('&', message.getMessage()));
                    }
                    sender.sendMessage("§6---- §3Do §7/om clear §3to delete all your messages! §6----§r");
                }
            }
        }
        else if (Check("reload", command)) {
            if (isPlayer) {
                if (PermissionsHelper.HasPermissionOrOp(playerSender, "om.reload")) {
                    OfflineMessenger.getInstance().ReloadAllConfigs(false);
                    logger.LogAny(ChatColor.RED + "Config Reloaded!");
                }
                else {
                    logger.LogAny(ChatColor.RED + "You dont have permission for this command");
                }
            }
            else {
                OfflineMessenger.getInstance().ReloadAllConfigs(false);
                logger.LogAny(ChatColor.RED + "Config Reloaded!");
            }
        }
        else {
            logger.LogAny(ChatColor.RED + "That command doesnt exist");
        }
    }

    public boolean onCommand(CommandSender commandSender, Command cmd, String s, String[] strings) {
        String command = ArgumentsParser.GetCommand(strings);
        ArrayList<String> args = ArgumentsParser.GetCommandArgs(strings);
        if (commandSender == null){
            ChatLogger.LogPlugin("Error in Command Handler: Sender was null");
            return true;
        }
        logger.setSender(commandSender);
        if (strings.length == 0){
            logger.LogAny(ChatColor.GREEN + "------- " + ChatColor.GRAY + "OfflineMessenger" + ChatColor.GREEN + " -------");
            logger.LogAny(ChatColor.GOLD + "A plugin for sending messages");
            logger.LogAny(ChatColor.GOLD + "to players when they next join");
            logger.LogAny(ChatColor.BLUE + "Version 1.2.173 :)");
            logger.LogAny(
                    ChatColor.GOLD + "Do " +
                            ChatColor.GREEN + "/om " +
                            ChatColor.DARK_GREEN + "help " +
                            ChatColor.GOLD + " for help");
            logger.LogAny(ChatColor.GREEN + "---------------------------------");
        }
        else {
            ExecuteCommand(command, ArgumentsParser.ToArray(args), commandSender);
        }

        return true;
    }
}
