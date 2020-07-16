package com.akoot.plugins.ultravanilla.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RawUtil {

    public static TextComponent getTextComponent(String color, String text) {
        TextComponent textComponent = new TextComponent(text);
        textComponent.setColor(ChatColor.of(color));
        return textComponent;
    }

    public static TextComponent getTextComponent(String color1, String color2, String text) {
        return new TextComponent(gradient(text, Color.decode(color1), Color.decode(color2)));
    }

    // Borrowed from BillyGalbreath#4747 on Discord
    public static String gradient(String str, Color one, Color two) {
        int l = str.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < l; i++) {
            sb.append(ChatColor.of(new Color(
                    (one.getRed() + (i * (1.0F / l) * (two.getRed() - one.getRed()))) / 255,
                    (one.getGreen() + (i * (1.0F / l) * (two.getGreen() - one.getGreen()))) / 255,
                    (one.getBlue() + (i * (1.0F / l) * (two.getBlue() - one.getBlue()))) / 255
            )));
            sb.append(str.charAt(i));
        }
        return sb.toString();
    }

    public void parseColors(String str) {
        List<Color> colors = new ArrayList<>();
    }

}
