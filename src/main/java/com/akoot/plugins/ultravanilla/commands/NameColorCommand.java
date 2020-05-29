package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NameColorCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GRAY;

    public NameColorCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                setNameColor(args[0], sender, (Player) sender);
            } else {
                sender.sendMessage(plugin.getString("player-only", "{action}", "set your name color"));
            }
        } else if (args.length == 2) {
            if (sender.hasPermission("ultravanilla.command.namecolor.other")) {
                Player player = plugin.getServer().getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(plugin.getString("player-offline", "{player}", args[1]));
                    return true;
                }
                setNameColor(args[0], sender, player);
            } else {
                sender.sendMessage(plugin.getString("no-permission", "{action}", "set other people's name color"));
            }
        } else {
            return false;
        }
        return true;
    }

    private void setNameColor(String nameColor, CommandSender sender, Player target) {
        try {
            ChatColor nameColorCode = ChatColor.valueOf(nameColor);
            UltraVanilla.getConfig(target).set("name-color", nameColor);
            target.setDisplayName(nameColorCode + target.getName());
            target.setPlayerListName(nameColorCode + target.getName());
        } catch (IllegalArgumentException e) {
            sendMessage(sender, "&4%s &cis not a valid color", nameColor.toUpperCase());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            return null;
        } else {
            List<String> suggestions = new ArrayList<>();
            for (ChatColor color : ChatColor.values()) {
                suggestions.add(color.name());
            }
            return suggestions;
        }
    }
}
