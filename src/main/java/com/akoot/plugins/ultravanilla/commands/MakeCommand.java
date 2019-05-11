package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
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
                        message = quote(message);
                    } else if (subCommand.equalsIgnoreCase("do")) {
                        player.performCommand(message);
                        message = "/" + message;
                    } else {
                        sender.sendMessage(String.format("Can't make anyone %s anything.", Palette.VERB + object(subCommand)));
                        return false;
                    }
                }
                sender.sendMessage(color + String.format("Made %s %s %s", noun(playerList(players)), number(subCommand), reset(message)));
            } else {
                sender.sendMessage(playerNotOnline(args[1]));
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
            addDefaults(list, args[1], "say", "do");
        }

        return list;
    }
}
