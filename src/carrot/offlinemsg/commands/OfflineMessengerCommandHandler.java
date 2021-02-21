package carrot.offlinemsg.commands;

import carrot.offlinemsg.OfflineMessenger;
import carrot.offlinemsg.PermissionsHelper;
import carrot.offlinemsg.logs.ChatLogger;
import carrot.offlinemsg.players.MessageParser;
import carrot.offlinemsg.players.MessageQueueManager;
import carrot.offlinemsg.players.PlayerMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.CharConversionException;
import java.util.ArrayList;

public class OfflineMessengerCommandHandler implements CommandExecutor {
    private ChatLogger logger = new ChatLogger(null);
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
            ParsedValue<Integer> parsedPage = ArgumentsParser.ParseInteger(args, 0);
            int page = 0;
            if (!parsedPage.failed()) {
                page = parsedPage.getValue();
            }
            if (isPlayer) {
                HelpDisplay.DisplayHelp(playerSender, page, false);
            }
            else {
                HelpDisplay.DisplayHelp(null, page, true);
            }
        }
        else if (Check("clear", command)) {
            if (isPlayer) {
                if (PermissionsHelper.HasPermission(playerSender, "om.clearPlayer")) {
                    ParsedValue<String> player = ArgumentsParser.ParseString(args, 0);
                    if (player.failed()) {
                        logger.LogAny(ChatColor.RED + "Error with the player name");
                        return;
                    }
                    MessageQueueManager.RemoveTarget(player.getValue());
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
        else if (Check("add", command)) {
            if (isPlayer) {
                if (!PermissionsHelper.HasPermission(playerSender, "om.add")) {
                    logger.LogAny(ChatColor.RED + "You dont have permission for this command");
                    return;
                }
            }
            ParsedValue<String> player = ArgumentsParser.ParseString(args, 0);
            ParsedValue<String> message = ArgumentsParser.ParseString(args, 1);
            if (player.failed()) {
                logger.LogAny("Error with the player's name");
                return;
            }
            if (message.failed()) {
                logger.LogAny("Error with the message to be queued");
                return;
            }
            PlayerMessage existingMessage = MessageQueueManager.GetQueuedMessage(player.getValue());
            if (existingMessage == null) {
                PlayerMessage playerMessage = new PlayerMessage(sender.getName(), player.getValue());
                String msg = ChatColor.translateAlternateColorCodes('&', message.getValue());
                msg = msg.replace('_', ' ');
                playerMessage.AddLine(sender.getName());
                playerMessage.AddLine(msg);
                logger.LogAny(ChatColor.GOLD + "Added: " + ChatColor.RESET + msg);
                MessageQueueManager.AddMessage(player.getValue(), playerMessage);
            }
            else{
                String msg = ChatColor.translateAlternateColorCodes('&', message.getValue());
                msg = msg.replace('_', ' ');
                existingMessage.AddLine(msg);
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
            ParsedValue<String> player = ArgumentsParser.ParseString(args, 0);
            ParsedValue<Integer> line = ArgumentsParser.ParseInteger(args, 1);
            if (player.failed()) {
                logger.LogAny("Error with the player's name");
                return;
            }
            if (line.failed()) {
                logger.LogAny("Error with the line number to be removed from a players queued message");
                return;
            }

            PlayerMessage message = MessageQueueManager.GetQueuedMessage(player.getValue());
            if (message == null) {
                logger.LogAny(ChatColor.GOLD + "That player doesn't have any queued messages");
                return;
            }
            if (message.RemoveLine(line.getValue())) {
                logger.LogAny(ChatColor.GREEN + "Removed line " + line.getValue());
            }
            else {
                logger.LogAny(ChatColor.GOLD + "Failed to remove line " + line.getValue());
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
            PlayerMessage playerMessage = MessageQueueManager.GetQueuedMessage(player.getValue());
            if (playerMessage != null) {
                ArrayList<String> messages = playerMessage.getMessages();
                for (int i = 1; i < messages.size(); i++) {
                    String line = messages.get(i);
                    sender.sendMessage(i + ": " + line);
                }
            }
            else{
                logger.LogAny(ChatColor.RED + "There are no queued messages for that player");
            }
        }
        else {
            logger.LogAny(ChatColor.RED + "That command doesnt exist");
        }
    }

    public boolean onCommand(CommandSender commandSender, Command cmd, String s, String[] strings) {
        String command = ArgumentsParser.GetCommand(strings);
        ArrayList<String> args = ArgumentsParser.GetCommandArgs(strings);
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
