package world.ultravanilla.commands;

import world.ultravanilla.UltraVanilla;
import world.ultravanilla.reference.Palette;
import world.ultravanilla.stuff.StringUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import world.ultravanilla.reference.Palette;
import world.ultravanilla.stuff.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UltraCommand {

    public ChatColor color = ChatColor.WHITE;
    protected UltraVanilla plugin;

    public UltraCommand(UltraVanilla instance) {
        this.plugin = instance;
    }

    protected String possessive(String item) {
        return item + (item.endsWith("s") ? "'" : "'s");
    }

    protected String fmt(String message, Object... args) {
        return color + Palette.translate(String.format(message.replace("&:", color + ""), args));
    }

    protected void sendFormatted(CommandSender sender, String message, Object... format) {
        sender.sendMessage(String.format(message, format));
    }

    protected void sendMessage(CommandSender sender, String message, Object... args) {
        sender.sendMessage(fmt(message, args));
    }

    protected String[] refinedArgs(String[] args) {
        return StringUtil.toArgs(String.join(" ", args));
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
                Player player = players.get(plugin.random().nextInt(players.size()));
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

    protected String getArgFor(String s, String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(s) && i + 1 < args.length) {
                return args[i + 1];
            }
        }
        return null;
    }

    protected boolean hasArg(String s, String[] args) {
        return Arrays.asList(args).contains(s);
    }

    /**
     * Get suggestions based on what the user has typed, like if they start typing the beginning of a suggestion then
     * only show the suggestions that start with that argument.
     *
     * @param suggestions All of the possible suggestions based on the argument count
     * @param args        The arguments the user has passed
     * @return The list of suggestions which are relevant to the query
     */
    protected List<String> getSuggestions(List<String> suggestions, String[] args) {
        List<String> realSuggestions = new ArrayList<>();
        for (String s : suggestions) {
            if (args[args.length - 1].length() < args.length || s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                realSuggestions.add(s);
            }
        }
        return realSuggestions;
    }
}
