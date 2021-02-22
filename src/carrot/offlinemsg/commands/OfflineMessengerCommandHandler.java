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
            if (isPlayer) {
                if (PermissionsHelper.HasPermissionOrOp(playerSender, "om.clearPlayer")) {
                    ParsedValue<String> player = ArgumentsParser.ParseString(args, 0);
                    if (player.failed()) {
                        MessageQueueManager.RemoveTarget(playerSender.getName());
                        logger.LogAny(ChatColor.GREEN + "Cleared your messages!");
                        return;
                    }
                    MessageQueueManager.RemoveTarget(player.getValue());
                    logger.LogAny(ChatColor.GREEN + "Cleared their messages!");
                }
                else {
                    MessageQueueManager.RemoveTarget(playerSender.getName());
                    logger.LogAny(ChatColor.GREEN + "Cleared your messages!");
                }
            }
            else {
                ParsedValue<String> player = ArgumentsParser.ParseString(args, 0);
                if (player.failed()) {
                    logger.LogAny(ChatColor.RED + "Error with the player name");
                    return;
                }
                MessageQueueManager.RemoveTarget(player.getValue());
                logger.LogAny(ChatColor.GOLD + "Cleared " + ChatColor.GREEN + player.getValue() + ChatColor.GOLD + " messages!");
            }
        }
        else if (Check("send", command)) {
            if (isPlayer) {
                if (!PermissionsHelper.HasPermissionOrOp(playerSender, "om.send")) {
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
            PlayerMessage existingMessage = null;
            ArrayList<PlayerMessage> messages = MessageQueueManager.GetQueuedMessages(parsedPlayer.getValue());
            if (messages == null) {
                PlayerMessage message = new PlayerMessage(sender.getName(), parsedPlayer.getValue(), parsedMessage.getValue());
                MessageQueueManager.AddMessagesToQueuedAndConfig(parsedPlayer.getValue(), message);
                logger.LogAny(ChatColor.GOLD + "Added: " + ChatColor.RESET + message.getMessages());
                OfflineMessenger.getInstance().SaveMessageQueueConfig();
                return;
            }
            for (int i = 0; i < messages.size(); i++) {
                PlayerMessage message = messages.get(i);
                if (message.getSender().equals(sender.getName())) {
                    existingMessage = message;
                    break;
                }
            }
            if (existingMessage == null) {
                PlayerMessage message = new PlayerMessage(sender.getName(), parsedPlayer.getValue(), parsedMessage.getValue());
                MessageQueueManager.AddMessagesToQueuedAndConfig(parsedPlayer.getValue(), message);
                logger.LogAny(ChatColor.GOLD + "Added: " + ChatColor.RESET + message.getMessages());
                OfflineMessenger.getInstance().SaveMessageQueueConfig();
            }
            else {
                String msg = ChatColor.translateAlternateColorCodes('&', parsedMessage.getValue());
                msg = msg.replace('_', ' ');
                existingMessage.setMessages(msg);
                MessageQueueManager.UpdateExistingMessageInConfig(existingMessage);
                logger.LogAny(ChatColor.GOLD + "Added: " + ChatColor.RESET + msg);
                OfflineMessenger.getInstance().SaveMessageQueueConfig();
            }
        }
        else if (Check("remove", command)) {
            if (isPlayer) {
                if (!PermissionsHelper.HasPermission(playerSender, "om.remove")) {
                    logger.LogAny(ChatColor.RED + "You dont have permission for this command");
                    return;
                }
            }
            ParsedValue<String> parsedPlayer = ArgumentsParser.ParseString(args, 0);
            if (parsedPlayer.failed()) {
                logger.LogAny("Error with the player's name");
                return;
            }

            PlayerMessage existingMessage = null;
            ArrayList<PlayerMessage> messages = MessageQueueManager.GetQueuedMessages(parsedPlayer.getValue());
            for (int i = 0; i < messages.size(); i++) {
                PlayerMessage message = messages.get(i);
                if (message.getSender().equals(sender.getName())) {
                    existingMessage = message;
                    break;
                }
            }
            if (existingMessage == null) {
                logger.LogAny(ChatColor.GOLD + "That player has no pending messages!");
            }
            else {
                MessageQueueManager.RemoveMessage(existingMessage.getTarget(), existingMessage.getSender());
                logger.LogAny(ChatColor.GOLD + "Removed that message!");
            }
        }
        else if (Check("show", command)) {
            if (isPlayer) {
                if (!PermissionsHelper.HasPermission(playerSender, "om.add")) {
                    logger.LogAny(ChatColor.RED + "You dont have permission for this command");
                    return;
                }
            }
            ParsedValue<String> player = ArgumentsParser.ParseString(args, 0);
            if (player.failed()) {
                logger.LogAny(ChatColor.RED + "Error with the player's name");
                return;
            }
            logger.LogAny(ChatColor.GOLD + "Displaying the queued messages for " + ChatColor.GREEN + player.getValue());
            ArrayList<PlayerMessage> messages = MessageQueueManager.GetQueuedMessages(player.getValue());
            if (messages == null || messages.size() < 1) {
                logger.LogAny(ChatColor.RED + "There are no queued messages for that player");
            }
            else {
                for (PlayerMessage message : messages) {
                    sender.sendMessage(ChatColor.GOLD + "From " + ChatColor.GREEN + message.getSender() + ": " + ChatColor.RESET + message.getMessages());
                }
            }
        }
        else if (Check("reload", command)) {
            if (isPlayer) {
                if (PermissionsHelper.HasPermission(playerSender, "om.reload")) {
                    OfflineMessenger.getInstance().ReloadAllConfigs();
                    logger.LogAny(ChatColor.RED + "Config Reloaded!");
                }
                else {
                    logger.LogAny(ChatColor.RED + "You dont have permission for this command");
                }
            }
            else {
                OfflineMessenger.getInstance().ReloadAllConfigs();
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
            logger.LogAny(ChatColor.GREEN + "------ " + ChatColor.GRAY + "OfflineMessenger" + ChatColor.GREEN + " ------");
            logger.LogAny(
                    ChatColor.GOLD + "Do " +
                            ChatColor.GREEN + "/om " +
                            ChatColor.DARK_GREEN + "help " +
                            ChatColor.YELLOW + "<page>" +
                            ChatColor.GOLD + " for help");
            logger.LogAny(ChatColor.GREEN + "-------------------------------");
        }
        else {
            ExecuteCommand(command, ArgumentsParser.ToArray(args), commandSender);
        }

        return true;
    }
}
