package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import com.akoot.plugins.ultravanilla.util.Palette;
import org.bukkit.ChatColor;

import java.util.List;

public class UltraCommand {

    protected Ultravanilla plugin;
    protected ChatColor color = ChatColor.WHITE;

    public UltraCommand(Ultravanilla instance) {
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

    protected String playerNotFound(String name) {
        return format("Player %s is not online!", noun(name));
    }

    protected void addDefaults(List<String> list, String arg, String... items) {
        for (String item : items) {
            if (item.startsWith(arg.toLowerCase())) {
                list.add(item);
            }
        }
    }
}
