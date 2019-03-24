package com.akoot.plugins.ultravanilla.util;

import org.bukkit.ChatColor;

public class Palette {
    public static final ChatColor NOUN = ChatColor.LIGHT_PURPLE;
    public static final ChatColor NUMBER = ChatColor.GOLD;
    public static final ChatColor OBJECT = ChatColor.AQUA;
    public static final ChatColor WRONG = ChatColor.RED;
    public static final ChatColor RIGHT = ChatColor.GREEN;
    public static final ChatColor TRUE = ChatColor.DARK_AQUA;
    public static final ChatColor FALSE = ChatColor.DARK_RED;

    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
