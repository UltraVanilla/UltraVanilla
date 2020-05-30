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

    public static final ChatColor COLOR = ChatColor.WHITE;

    public NameColorCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                if (args[0].contains(",")) {
                    sendMessage(sender, "Please specify where you want the 2 colors");
                } else {
                    Player player = (Player) sender;
                    try {
                        ChatColor color = ChatColor.valueOf(args[0].toUpperCase());
                        setNameColor(player, color);
                        sendMessage(sender, "Updated &dyour&: name to &r%s", player.getDisplayName());
                    } catch (IllegalArgumentException e) {
                        sendMessage(sender, "&c%s &:is an invalid color!", args[0]);
                    }
                }
            } else {
                sender.sendMessage(plugin.getString("player-only", "{action}", "set your name color"));
            }
        } else if (args.length == 2) {
            String colors = args[0];
            if (colors.contains(",")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (sender.hasPermission("ultravanilla.command.namecolor.two")) {
                        String displayName = ChatColor.stripColor(player.getDisplayName());
                        String format = args[1];
                        if (displayName.equals(format.replace(",", ""))) {

                            try {
                                int i = colors.indexOf(",");
                                ChatColor color1 = ChatColor.valueOf(colors.substring(0, i).toUpperCase());
                                ChatColor color2 = ChatColor.valueOf(colors.substring(i + 1).toUpperCase());

                                int j = format.indexOf(",");
                                String name1 = format.substring(0, j);
                                String name2 = format.substring(j + 1).replace(",", "");

                                String coloredName = color1 + name1 + color2 + name2;
                                UltraVanilla.set(player, "display-name", coloredName);
                                UltraVanilla.updateDisplayName(player);
                                sendMessage(sender, "Updated &dyour&: name to &r%s", coloredName);
                            } catch (IllegalArgumentException e) {
                                sendMessage(sender, "&c%s &:contains invalid colors!", args[0]);
                            }
                        } else {
                            tutorial(player);
                        }
                    } else {
                        sender.sendMessage(plugin.getString("no-permission", "{action}", "have 2 name colors"));
                    }
                } else {
                    sender.sendMessage(plugin.getString("player-only", "{action}", "set your name color"));
                }
            } else {
                Player player = plugin.getServer().getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(plugin.getString("player-offline", "{player}", args[1]));
                    return true;
                }
                try {
                    ChatColor color = ChatColor.valueOf(args[0].toUpperCase());
                    setNameColor(player, color);
                    sendMessage(sender, "Updated &d%s&:'s name to &r%s", player.getName(), player.getDisplayName());
                } catch (IllegalArgumentException e) {
                    sendMessage(sender, "&c%s &:is an invalid color name!", args[0]);
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private void tutorial(Player player) {
        String displayName = ChatColor.stripColor(player.getDisplayName());
        int middle = (int) Math.round(displayName.length() / 2.0);
        String part1 = displayName.substring(0, middle);
        String part2 = displayName.substring(middle);
        sendMessage(player, "Specify where you want to split with a comma.\n" +
                        "Example: &7/namecolor &bblue,red %s\n&:Gives you &r%s",
                part1 + "," + part2,
                ChatColor.BLUE + part1 + ChatColor.RED + part2);
    }

    private void setNameColor(Player player, ChatColor color) {
        String displayName = ChatColor.stripColor(player.getDisplayName());
        UltraVanilla.set(player, "display-name", color + displayName);
        UltraVanilla.updateDisplayName(player);
    }

    private void setNameColor(String nameColor, CommandSender sender, Player target) {
        try {
            ChatColor nameColorCode = ChatColor.valueOf(nameColor);
            UltraVanilla.set(target, "name-color", nameColor);
            String coloredName = nameColorCode + ChatColor.stripColor(target.getDisplayName());
            target.setDisplayName(coloredName);
            target.setPlayerListName(coloredName);
        } catch (IllegalArgumentException e) {
            sendMessage(sender, "&4%s &cis not a valid color", nameColor);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            for (ChatColor color : ChatColor.values()) {
                suggestions.add(color.name().toLowerCase());
                if (sender.hasPermission("ultravanilla.command.namecolor.two")) {
                    for (ChatColor color2 : ChatColor.values()) {
                        suggestions.add(color.name().toLowerCase() + "," + color2.name().toLowerCase());
                    }
                }
            }
        } else if (args.length == 2) {
            if (sender instanceof Player && sender.hasPermission("ultravanilla.command.namecolor.two") && args[0].contains(",")) {
                String name = ChatColor.stripColor(((Player) sender).getDisplayName());
                char[] charArray = name.toCharArray();
                for (int i = 1; i < charArray.length; i++) {
                    suggestions.add(name.substring(0, i) + "," + name.substring(i));
                }
            } else {
                if (sender.hasPermission("ultravanilla.command.namecolor.other")) {
                    return null;
                } else {
                    return suggestions;
                }
            }
        }
        return getSuggestions(suggestions, args);
    }
}
