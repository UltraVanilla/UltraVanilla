package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.DARK_GREEN;

    public UserCommand(Ultravanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        args = refinedArgs(args);
        boolean print = !Arrays.asList(args).contains("-silent");
        if (args.length > 0) {

            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(args[0]);
            YamlConfiguration config = Ultravanilla.getConfig(offlinePlayer.getUniqueId());

            if (config == null) {
                sender.sendMessage(format("User %s not found", quote(noun(args[0]))));
                return true;
            }

            if (args.length == 1) {
                sender.sendMessage(format("%s info", posessiveNoun(offlinePlayer.getName())));
                for (String key : config.getKeys(true)) {
                    sender.sendMessage(format("%s: %s", key, reset(config.get(key) + "")));
                }
            } else if (args.length >= 3) {

                String key = args[2];

                if (args.length == 3) {
                    if (!config.contains(key)) {
                        sender.sendMessage(format("Key %s not found", quote(object(key))));
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("get")) {
                        sender.sendMessage(format("%s: %s", key, reset(config.get(key).toString())));
                    } else if (args[1].equalsIgnoreCase("clear")) {
                        Ultravanilla.set(offlinePlayer.getUniqueId(), key, null);
                        if (print) {
                            sender.sendMessage(format("Cleared %s for %s", quote(object(key)), noun(offlinePlayer.getName())));
                        }
                    } else {
                        sender.sendMessage("ex0");
                        return false;
                    }
                } else if (args.length == 4) {
                    if (args[1].equalsIgnoreCase("set")) {
                        Ultravanilla.set(offlinePlayer.getUniqueId(), key, args[3]);
                        if (print) {
                            sender.sendMessage(format("Set %s to %s for %s", quote(object(key)), quote(object(args[3])), noun(offlinePlayer.getName())));
                        }
                    } else {
                        sender.sendMessage("ex3");
                        return false;
                    }
                }
            } else {
                sender.sendMessage("ex2");
                return false;
            }
        } else {
            sender.sendMessage("ex1");
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            return null;
        } else if (args.length > 1) {
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
            if (player.hasPlayedBefore()) {
                if (args.length == 2) {
                    suggestions.add("set");
                    suggestions.add("clear");
                    suggestions.add("get");
                } else if (args.length >= 3) {
                    YamlConfiguration config = Ultravanilla.getConfig(player.getUniqueId());
                    if (config != null) {
                        if (args.length == 3) {
                            suggestions.addAll(config.getKeys(true));
                        } else if (args.length >= 4) {
                            Object value = config.get(args[2]);
                            if (value != null) {
                                suggestions.add(value.toString());
                            }
                        }
                    }
                }
            }
        }
        return suggestions;
    }
}
