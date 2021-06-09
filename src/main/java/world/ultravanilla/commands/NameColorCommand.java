package world.ultravanilla.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import world.ultravanilla.UltraVanilla;
import world.ultravanilla.reference.LegacyColors;
import world.ultravanilla.reference.Palette;

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
                Player player = (Player) sender;
                if (args[0].contains(Palette.MIX_SYMBOL)) {
                    if (sender.hasPermission("ultravanilla.command.namecolor.gradient")) {
                        try {
                            String colors = args[0];
                            int i = colors.indexOf(Palette.MIX_SYMBOL);
                            String color1 = colors.substring(0, i).toLowerCase();
                            String color2 = colors.substring(i + 1).toLowerCase();
                            String strippedName = ChatColor.stripColor(player.getDisplayName());
                            UltraVanilla.set(player, "display-name", Palette.gradient(strippedName, color1, color2));
                            UltraVanilla.updateDisplayName(player);
                            sendMessage(sender, "Updated &dyour&: name to &r%s", player.getDisplayName());
                        } catch (IllegalArgumentException e) {
                            sendMessage(sender, "&c%s &:is an invalid color!", args[0]);
                        }
                    }
                } else {
                    try {
                        ChatColor color = ChatColor.of(args[0].toLowerCase());
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
            if (colors.contains(Palette.MIX_SYMBOL)) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (sender.hasPermission("ultravanilla.command.namecolor.two")) {
                        String displayName = ChatColor.stripColor(player.getDisplayName());
                        String format = args[1];
                        if (displayName.equals(format.replace(Palette.MIX_SYMBOL, ""))) {

                            try {
                                int i = colors.indexOf(Palette.MIX_SYMBOL);
                                ChatColor color1 = ChatColor.of(colors.substring(0, i).toLowerCase());
                                ChatColor color2 = ChatColor.of(colors.substring(i + 1).toLowerCase());

                                int j = format.indexOf(Palette.MIX_SYMBOL);
                                String name1 = format.substring(0, j);
                                String name2 = format.substring(j + 1).replace(Palette.MIX_SYMBOL, "");

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
                if (sender.hasPermission("ultravanilla.namecolor.other")) {
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
                } else {
                    sender.sendMessage(plugin.getString("no-permission", "{action}", "change other people's name colors"));
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
        ChatColor randomColor1 = Palette.getRandomColor();
        ChatColor randomColor2 = Palette.getRandomColor();

        while (randomColor2.equals(randomColor1)) {
            randomColor1 = Palette.getRandomColor();
        }
        sendMessage(player, "Specify where you want to split with a plus (+).\n" +
                        "Example: &7/namecolor &b%s,%s %s\n&:Gives you &r%s", randomColor1.getName(), randomColor2.getName(),
                part1 + Palette.MIX_SYMBOL + part2,
                randomColor1 + part1 + randomColor2 + part2);
    }

    private void setNameColor(Player player, ChatColor color) {
        String displayName = ChatColor.stripColor(player.getDisplayName());
        UltraVanilla.set(player, "display-name", color + displayName);
        UltraVanilla.updateDisplayName(player);
    }

    private void setNameColor(String nameColor, CommandSender sender, Player target) {
        try {
            ChatColor nameColorCode = ChatColor.of(nameColor);
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
            for (String color1 : LegacyColors.listNames()) {
                suggestions.add(color1);
                if (sender.hasPermission("ultravanilla.command.namecolor.gradient") || sender.hasPermission("ultravanilla.command.namecolor.two")) {
                    for (String color2 : LegacyColors.listNames()) {
                        if (!color1.equals(color2)) {
                            suggestions.add(color1 + Palette.MIX_SYMBOL + color2);
                        }
                    }
                }
            }
        } else if (args.length == 2) {
            if (sender instanceof Player && sender.hasPermission("ultravanilla.command.namecolor.two")
                    && args[0].contains(Palette.MIX_SYMBOL)) {
                String name = ChatColor.stripColor(((Player) sender).getDisplayName());
                char[] charArray = name.toCharArray();
                for (int i = 1; i < charArray.length; i++) {
                    suggestions.add(name.substring(0, i) + Palette.MIX_SYMBOL + name.substring(i));
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
