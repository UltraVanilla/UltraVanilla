package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UltraCommand {

    public ChatColor color = ChatColor.WHITE;
    protected UltraVanilla plugin;

    public UltraCommand(UltraVanilla instance) {
        this.plugin = instance;
    }

    protected String posessive(String item) {
        return item + (item.endsWith("s") ? "'" : "'s");
    }

    protected String[] refinedArgs(String[] args) {
        Pattern pattern = Pattern.compile("\"[^\"]+\"|[-\\w]+");
        Matcher matcher = pattern.matcher(String.join(" ", args));
        List<String> refined = new ArrayList<>();
        while (matcher.find()) {
            refined.add(matcher.group().replace("\"", ""));
        }
        return refined.toArray(new String[0]);
    }

    protected String getArgFor(String[] args, String arg) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(arg)) {
                if (i + 1 < args.length) {
                    return args[i + 1];
                }
            }
        }
        return null;
    }

    protected String getArg(String[] args) {
        String arg = String.join(" ", args);
        return arg.trim();
    }

    protected String getArg(String[] args, int index) {
        String message = "";
        for (int i = index - 1; i < args.length; i++) {
            message += args[i] + " ";
        }
        return message.trim();
    }

    protected String playerList(List<Player> players) {
        String list = "";
        if (players.size() == 1) {
            return players.get(0).getName();
        }
        for (int i = 0; i < players.size(); i++) {
            if (i == players.size() - 1) {
                list += "and " + players.get(i).getName();
            } else {
                list += players.get(i).getName() + ", ";
            }
        }
        return list;
    }

    protected List<Player> getPlayers(String arg) {
        List<Player> players = new ArrayList<>();
        if (arg.equals("@a")) {
            players.addAll(plugin.getServer().getOnlinePlayers());
        } else if (arg.equalsIgnoreCase("@r")) {
            players.addAll(plugin.getServer().getOnlinePlayers());
            if (players.size() > 0) {
                Player player = players.get(plugin.getRandom().nextInt(players.size()));
                players = new ArrayList<>();
                players.add(player);
            }
        } else if (arg.contains(",")) {
            for (String name : arg.split(",")) {
                Player player = plugin.getServer().getPlayer(name);
                if (player != null) {
                    players.add(player);
                }
            }
        } else {
            Player player = plugin.getServer().getPlayer(arg);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    protected String format(Command command, String key) {
        String message = plugin.getCommandString(command.getName() + "." + key);
        if (message != null) {
            message = message.replace("$color", color + "");
        } else {
            message = key;
        }
        return color + Palette.translate(message);
    }

    protected String format(Command command, String key, String... format) {
        String message = plugin.getCommandString(command.getName() + "." + key);
        if (message != null) {
            for (int i = 0; i < format.length; i += 2) {
                message = message.replace(format[i], format[i + 1]);
            }
            message = message.replace("$color", color + "");
        } else {
            message = key;
        }
        return color + Palette.translate(message);
    }

    protected String message(Command command, String key, String... format) {
        return format(command, "message." + key, format);
    }

    protected String error(Command command, String key, String... format) {
        return format(command, "error." + key, format);
    }

    protected int getInt(CommandSender sender, String arg) {
        try {
            return Integer.valueOf(arg);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getString("not-a-number", "{number}", arg));
            return -1;
        }
    }
}
