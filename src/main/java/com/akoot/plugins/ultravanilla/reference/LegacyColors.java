package com.akoot.plugins.ultravanilla.reference;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;

public class LegacyColors {

    public static String[] listNames() {
        Hex[] values = Hex.values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name().toLowerCase();
        }
        return names;
    }

    public static Color getColor(String str) {
        Color color;
        if (str.startsWith("#")) {
            color = Color.decode(str);
        } else if (str.length() == 1) {
            color = LegacyColors.getColor(ChatColor.getByChar(str.toCharArray()[0]));
        } else {
            color = LegacyColors.getColor(ChatColor.of(str.toLowerCase()));
        }
        return color;
    }

    public static Color getColor(ChatColor color) {
        return Color.decode(Hex.valueOf(color.getName().toUpperCase()).value());
    }

    private enum Hex {

        BLACK("#000000"),
        DARK_BLUE("#0000AA"),
        DARK_GREE("#0AA00"),
        DARK_AQUA("#00AAAA"),
        DARK_RE("#A0000"),
        DARK_PURPLE("#AA00AA"),
        GOLD("#FFAA00"),
        GRAY("#AAAAAA"),
        DARK_GRAY("#555555"),
        BLUE("#5555FF"),
        GREEN("#55FF55"),
        AQUA("#55FFFF"),
        RED("#FF5555"),
        LIGHT_PURPLE("#FF55FF"),
        YELLOW("#FFFF55"),
        WHITE("#FFFFFF");

        String hexCode;

        Hex(String hexCode) {
            this.hexCode = hexCode;
        }

        String value() {
            return hexCode;
        }

    }

}
