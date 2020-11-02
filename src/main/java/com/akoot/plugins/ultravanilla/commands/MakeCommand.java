package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MakeCommand extends UltraCommand implements CommandExecutor, TabCompleter {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public MakeCommand(UltraVanilla plugin) {
        super(plugin);
        color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length >= 3) {

            String subCommand = args[1];
            String message = "";

            for (int i = 2; i < args.length; i++) {
                message = message + args[i] + " ";
            }
            message = message.trim();

            List<Player> players = getPlayers(args[0]);
            if (!players.isEmpty()) {
                for (Player player : getPlayers(args[0])) {
                    if (subCommand.equalsIgnoreCase("say")) {
                        player.chat(message);
                    } else if (subCommand.equalsIgnoreCase("do")) {
                        player.performCommand(message);
                        message = "/" + message;
                    } else {
                        sender.sendMessage(format(command, "error.wrong-verb", "{player}", player.getName(), "{verb}", subCommand));
                        return false;
                    }
                }
                sender.sendMessage(format(command, "message.made", "{player}", playerList(players), "{verb}", subCommand, "{action}", message));
            } else {
                sender.sendMessage(plugin.getString("player-offline", "{player}", args[2]));
            }
            return true;
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
            list.add("say");
            list.add("do");
        }

        return list;
    }
}
