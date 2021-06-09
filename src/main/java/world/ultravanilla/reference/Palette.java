package world.ultravanilla.reference;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Palette {

    public static final char[] rainbowseq = {'a', '3', '9', '5', 'd', 'c', '6', 'e'};
    public static final String colorMatch = "(" + String.join("|", LegacyColors.listNames()) + "|#[0-9a-fA-F]{6}|[0-9a-fA-F])";
    public static final String MIX_SYMBOL = "+";
    public static final ChatColor VERB = ChatColor.ITALIC;
    public static final ChatColor NOUN = ChatColor.of("#3772ff");
    public static final ChatColor NUMBER = ChatColor.of("#F7D27E");
    public static final ChatColor OBJECT = ChatColor.of("#b8dbd9");
    public static final ChatColor TRUE = ChatColor.of("#8cba80");
    public static final ChatColor FALSE = ChatColor.of("#dd2d4a");
    public static final ChatColor RIGHT = ChatColor.of("#cacf85");
    public static final ChatColor WRONG = ChatColor.of("#880d1e");
    private static final Random random = new Random();

    public static ChatColor random() {
        return ChatColor.of(Color.getHSBColor(random.nextFloat(), 0.6f, 0.95f));
    }

    public static String BOOLEAN(boolean bool) {
        return (bool ? Palette.TRUE : Palette.FALSE) + "" + bool;
    }

    public static String untranslate(String str) {
        str = str.replaceAll("§", "&");
        Pattern pattern = Pattern.compile("&x((&[a-zA-Z0-9]){6})");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String line = "&#" + matcher.group(1).replaceAll("&", "").toLowerCase();
            str = str.replaceAll(matcher.group(0), line);
        }
        return str;
    }

    public static List<String> untranslate(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String line : list) {
            newList.add(untranslate(line));
        }
        return newList;
    }

    public static List<String> translate(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String line : list) {
            newList.add(translate(line));
        }
        return newList;
    }

    public static String translate(String text) {

        // Rainbow text
        if (text.contains("&x")) {
            Pattern p = Pattern.compile("&x([^&$]+)");
            Matcher m = p.matcher(text);
            while (m.find()) {
                text = text.replace(m.group(), rainbow(m.group(1)));
            }
        }

        if (text.contains("&#")) {
            Pattern p = Pattern.compile("&(#[0-9a-fA-F]{6})");
            Matcher m = p.matcher(text);
            while (m.find()) {
                text = text.replace(m.group(), ChatColor.of(m.group(1)) + "");
            }
        }

        if (text.contains("&>")) {
            Pattern p = Pattern.compile("&>" + colorMatch + "\\" + MIX_SYMBOL + colorMatch + "([^&$]+)");
            Matcher m = p.matcher(text);
            while (m.find()) {
                text = text.replace(m.group(), gradient(m.group(3), m.group(1), m.group(2)));
            }
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

    public static ChatColor getRandomColor() {
        return ChatColor.getByChar(rainbowseq[(int) (Math.random() * rainbowseq.length)]);
    }

    public static String gradient(String str, ChatColor color1, ChatColor color2) {
        return gradient(str, LegacyColors.getColor(color1), LegacyColors.getColor(color2));
    }

    public static String gradient(String str, String color1, String color2) {
        return gradient(str, LegacyColors.getColor(color1), LegacyColors.getColor(color2));
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
            float[] interpolatedCie = new float[]{
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

    public static String getHex(ChatColor color) {
        return String.format("#%06x", color.getColor().getRGB() & 0xffffff);
    }
}
