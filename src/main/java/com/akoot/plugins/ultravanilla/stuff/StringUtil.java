package com.akoot.plugins.ultravanilla.stuff;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static String getTimeString(long ms) {

        long seconds = (ms / 1000) % 60;
        long minutes = (ms / (1000 * 60)) % 60;
        long hours = (ms / (1000 * 60 * 60)) % 24;
        long days = (ms / (1000 * 60 * 60 * 24));

        List<String> strings = new ArrayList<>();
        if (days != 0) strings.add(plural(days, "day"));
        if (hours != 0) strings.add(plural(hours, "hour"));
        if (minutes != 0) strings.add(plural(minutes, "minute"));
        if (seconds != 0) strings.add(plural(seconds, "second"));

        if (strings.size() > 1) {
            strings.set(strings.size() - 1, "and " + strings.get(strings.size() - 1));
        }

        return String.join(" ", strings);
    }

    public static String plural(long value, String name) {
        String result = value + " " + name;
        if (value == 1) {
            return result;
        }
        return result + "s";
    }

    public static int getSeconds(String timeString) {
        int time = 0;
        Pattern p = Pattern.compile("([0-9]+(\\.[0-9]+)?)([dhmst])");
        Matcher m = p.matcher(timeString);
        while (m.find()) {
            double t = Double.parseDouble(m.group(1));
            double seconds = 0;
            switch (m.group(3)) {
                case "t":
                    seconds = 0.05;
                    break;
                case "s":
                    seconds = 1;
                    break;
                case "m":
                    seconds = 60;
                    break;
                case "h":
                    seconds = 3600;
                    break;
                case "d":
                    seconds = 86400;
                    break;
            }
            time += t * seconds;
        }
        return time;
    }
}
