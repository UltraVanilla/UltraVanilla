package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public ConfigCommand(Ultravanilla instance) {
        super(instance);
        this.color = ChatColor.GREEN;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /* TODO:
        /config name set some.key value
        /config name add some.key value
        /config name remove some.key value
         */

        if (args.length >= 3) {

            YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
            String configName = "config";
            String action = args[0];
            String key = args[1];
            String value = getArg(args, 2);

            if (!(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add-to") || args[0].equalsIgnoreCase("remove-from"))) {
                configName = args[0];
                config = plugin.getEditableConfig(args[0]);
                action = args[1];
                key = args[2];
                value = args[3];
            }

            if (config != null) {
                if (action.equalsIgnoreCase("set")) {
                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                        config.set(key, Boolean.parseBoolean(value));
                    }
                    config.set(key, value);
                    sender.sendMessage(format("Set %s to %s in %s!", object(key), object(value), noun(configName)));
                } else if (action.equalsIgnoreCase("add-to")) {
                    List<String> list = config.getStringList(key);
                    list.add(value);
                    config.set(key, list);
                    sender.sendMessage(format("Added %s to %s in %s!", object(value), object(key), noun(configName)));
                } else if (action.equalsIgnoreCase("remove-from")) {
                    List<String> list = config.getStringList(key);
                    list.remove(value);
                    config.set(key, list);
                    sender.sendMessage(format("Removed %s from %s in %s!", object(value), object(key), noun(configName)));
                } else {
                    sender.sendMessage(format("%s is not a valid action!", wrong(action)));
                }
            } else {
                sender.sendMessage(format("%s is not an editable configuration!", wrong(args[0])));
            }

            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        //TODO: fix suggestions

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("set");
            suggestions.add("add-to");
            suggestions.add("remove-from");
            suggestions.add("colors");
            suggestions.add("swears");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add-to") || args[0].equalsIgnoreCase("remove-from")) {
                YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
                suggestions.addAll(config.getKeys(true));
            } else {
                suggestions.add("set");
                suggestions.add("add-to");
                suggestions.add("remove-from");
            }
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("add-to") || args[1].equalsIgnoreCase("remove-from")) {
                YamlConfiguration config = plugin.getEditableConfig(args[0]);
                if (config != null) {
                    suggestions.addAll(config.getKeys(true));
                }
            } else {
                Object value = plugin.getConfig().get(args[1]);
                if (value != null) {
                    suggestions.add(value.toString());
                }
            }
        } else if (args.length == 4) {
            if (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("add-to") || args[1].equalsIgnoreCase("remove-from")) {
                YamlConfiguration config = plugin.getEditableConfig(args[0]);
                if (config != null) {
                    Object value = config.get(args[2]);
                    if (value != null) {
                        suggestions.add(value.toString());
                    }
                }
            }
        }
        return suggestions;
    }
}
