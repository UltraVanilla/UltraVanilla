package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MakeCommand extends UltraCommand implements CommandExecutor, TabCompleter {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public MakeCommand(Ultravanilla plugin) {
        super(plugin);
        color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length >= 3) {

            String playerName = args[0];
            String subCommand = args[1];
            String message = "";

            for (int i = 2; i < args.length; i++) {
                message = message + args[i] + " ";
            }
            message = message.trim();

            Player player = plugin.getServer().getPlayer(playerName);

            if (player != null) {
                if (subCommand.equalsIgnoreCase("say")) {
                    player.chat(message);
                    message = quote(message);
                } else if (subCommand.equalsIgnoreCase("do")) {
                    player.performCommand(message);
                    message = "/" + message;
                } else {
                    sender.sendMessage(String.format("Can't make %s \"%s\" anything.", noun(playerName), ChatColor.ITALIC + number(subCommand)));
                    return false;
                }
                sender.sendMessage(color + String.format("Made %s %s %s", noun(player.getName()), number(subCommand), reset(message)));
                return true;
            } else {
                sender.sendMessage(playerNotFound(args[1]));
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        // Initialize the list as empty
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            return null;
        } else if (args.length == 2) {
            addDefaults(list, args[1], "say", "do");
        }

        return list;
    }
}
