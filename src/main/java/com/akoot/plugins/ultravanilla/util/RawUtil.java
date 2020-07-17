package com.akoot.plugins.ultravanilla.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.awt.*;
import java.awt.color.ColorSpace;
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

    // implementation by lordpipe
    public static String gradient(String str, Color from, Color to) {
        StringBuilder sb = new StringBuilder();

        ColorSpace cie = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
        ColorSpace srgb = ColorSpace.getInstance(ColorSpace.CS_sRGB);

        float[] cieFrom = cie.fromRGB(from.getRGBColorComponents(null));
        float[] cieTo = cie.fromRGB(to.getRGBColorComponents(null));

        for (int i = 0, l = str.length(); i < l; i++) {
            // do interpolation in CIE space
            float[] interpolatedCie = new float[] {
                    cieFrom[0] + (i * (1.0F / l)) * (cieTo[0] - cieFrom[0]),
                    cieFrom[1] + (i * (1.0F / l)) * (cieTo[1] - cieFrom[1]),
                    cieFrom[2] + (i * (1.0F / l)) * (cieTo[2] - cieFrom[2])
            };

            // we could just pass the CIE value directly into `new Color`, but it seems the ChatColor API expects the
            // conversion to sRGB to be pre-computed, so it fails
            float[] interpolatedSrgb = srgb.fromCIEXYZ(interpolatedCie);
            sb.append(ChatColor.of(new Color(interpolatedSrgb[0], interpolatedSrgb[1], interpolatedSrgb[2])));
            sb.append(str.charAt(i));
        }
        return sb.toString();
    }

    public void parseColors(String str) {
        List<Color> colors = new ArrayList<>();
    }

}
