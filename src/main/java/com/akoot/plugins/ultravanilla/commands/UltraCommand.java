package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import org.bukkit.ChatColor;
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

    protected String playerOnly() {
        return color + "You are not a player!";
    }

    protected String quote(String item) {
        return "\"" + item + "\"";
    }

    protected String isNaN(String item) {
        return format("%s is not a number!", number(quote(item)));
    }

    protected String reset(String item) {
        return ChatColor.RESET + item + color;
    }

    protected String format(String item, Object... format) {
        return color + String.format(item, format);
    }

    protected String noun(String item) {
        return Palette.NOUN + item + color;
    }

    protected String posessiveNoun(String item) {
        return noun(item + (item.endsWith("s") ? "'" : "'s"));
    }

    protected String number(String item) {
        return Palette.NUMBER + item + color;
    }

    protected String object(String item) {
        return Palette.OBJECT + item + color;
    }

    protected String wrong(String item) {
        return Palette.WRONG + item + color;
    }

    protected String right(String item) {
        return Palette.RIGHT + item + color;
    }

    protected String bool(boolean bool) {
        return (bool ? Palette.TRUE : Palette.FALSE) + "" + bool + color;
    }

    protected String title(String item) {
        return color + "------ " + reset(item) + color + " ------";
    }

    protected String playerNotOnline(String name) {
        return format("%s is not online!", noun(name));
    }

    protected String playerNotFound(String name) {
        return format("%s has never joined!", noun(name));
    }


    protected String noPermission(String item) {
        return format(Palette.WRONG + "You do not have permission to " + Palette.FALSE + item);
    }

    protected void addDefaults(List<String> list, String arg, String... items) {
        for (String item : items) {
            if (item.startsWith(arg.toLowerCase())) {
                list.add(item);
            }
        }
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

    protected List<String> suggestPlayers() {
        List<String> suggestions = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            suggestions.add(player.getName());
        }
        suggestions.add("@a");
        suggestions.add("@r");
        return suggestions;
    }

    protected String playerList(List<Player> players) {
        String list = "";
        if (players.size() == 1) {
            return players.get(0).getName();
        }
        for (int i = 0; i < players.size(); i++) {
            if (i == players.size() - 1) {
                list += "and " + players.get(i);
            } else {
                list += players.get(i) + ", ";
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

    protected int getInt(CommandSender sender, String arg) {
        try {
            return Integer.valueOf(arg);
        } catch (NumberFormatException e) {
            sender.sendMessage(format(wrong("%s is not a number..."), quote(arg)));
            return -1;
        }
    }
}
