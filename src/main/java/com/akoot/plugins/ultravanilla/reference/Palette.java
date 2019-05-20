package com.akoot.plugins.ultravanilla.reference;

import org.bukkit.ChatColor;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Palette {

    private static Random random = new Random();

    public static final char[] rainbowseq = {'a', '3', '9', '5', 'd', 'c', '6', 'e'};

    public static final ChatColor NOUN = ChatColor.LIGHT_PURPLE;
    public static final ChatColor VERB = ChatColor.ITALIC;
    public static final ChatColor NUMBER = ChatColor.GOLD;
    public static final ChatColor OBJECT = ChatColor.AQUA;
    public static final ChatColor WRONG = ChatColor.RED;
    public static final ChatColor RIGHT = ChatColor.GREEN;
    public static final ChatColor TRUE = ChatColor.DARK_AQUA;
    public static final ChatColor FALSE = ChatColor.DARK_RED;

    public static String BOOLEAN(boolean bool) {
        return (bool ? Palette.TRUE : Palette.FALSE) + "" + bool;
    }

    public static String translate(String text) {

        // Rainbow text
        if (text.contains("&x")) {
            String toColor = getRegex("&x[^&]*", text);
            text = text.replace(toColor, rainbow(toColor.substring(2)));
        }

        // Random color text
        if (text.contains("&h")) {
            text = text.replace("&h", "" + ChatColor.values()[random.nextInt(ChatColor.values().length)]);
        }

        //TODO: read from config
        text = text
                .replace("$noun", NOUN + "")
                .replace("$verb", VERB + "")
                .replace("$number", NUMBER + "")
                .replace("$object", OBJECT + "")
                .replace("$wrong", WRONG + "")
                .replace("$right", RIGHT + "")
                .replace("$true", TRUE + "")
                .replace("$false", FALSE + "")
        ;

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String rainbow(String msg) {
        String rainbow = "";
        int i = random.nextInt(rainbowseq.length);
        for (char c : msg.toCharArray()) {
            if (i >= rainbowseq.length) {
                i = 0;
            }

            String ch = String.valueOf(c);
            if (c != ' ') {
                ch = "&" + rainbowseq[i] + ch;
                i++;
            }
            rainbow += ch;
        }
        return rainbow;
    }

    public static String getRegex(String regex, String data) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) data = matcher.group(0);
        return data;
    }
}
