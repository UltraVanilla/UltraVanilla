package com.akoot.plugins.ultravanilla.stuff;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * Combines arguments separated by a space into one single argument using double quotes or single quotes.
     * Use '/' as a delimiter.
     *
     * @param str The string to convert to arguments
     * @return Arguments with spaces
     */
    public static String[] toArgs(String str) {
        Pattern pattern = Pattern.compile("\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|'([^'\\\\]*(\\\\.[^'\\\\]*)*)'|[\\S]+");
        Matcher matcher = pattern.matcher(str);
        List<String> refined = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group();
            if (group.matches("^\".+\"$")) {
                refined.add(matcher.group(1).replaceAll("\\\\\"", "\""));
            } else if (group.matches("^'.+'$")) {
                refined.add(matcher.group(3).replaceAll("\\\\'", "'"));
            } else {
                refined.add(group);
            }
        }
        return refined.toArray(new String[0]);
    }

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

        return join(" ", strings);
    }

    public static String getSqlDate(long date) {
        if (date > 0) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            return df.format(new Date(date));
        } else {
            return "NULL";
        }
    }

    public static String join(String delimiter, List<String> strings) {
        if (strings.size() > 1) {
            strings.set(strings.size() - 1, "and " + strings.get(strings.size() - 1));
        }

        return String.join(delimiter, strings);
    }

    public static String plural(long value, String name) {
        String result = value + " " + name;
        if (value == 1) {
            return result;
        }
        return result + "s";
    }

    public static double getSeconds(String timeString) {
        double time = 0;
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
