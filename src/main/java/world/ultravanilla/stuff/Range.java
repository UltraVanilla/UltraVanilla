package world.ultravanilla.stuff;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Range {

    public float min;
    public float max;

    public Range(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public Range(float max) {
        this(0, max);
    }

    public static Range of(String str) {
        if (str.matches("\\d+(?:\\.\\d+)?")) {
            return new Range(Float.parseFloat(str));
        }
        Pattern p = Pattern.compile("(\\d+(?:.\\d)?),(\\d+(?:.\\d)?)");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return new Range(Float.parseFloat(m.group(1)), Float.parseFloat(m.group(2)));
        }
        return null;
    }

    public float getRandom() {
        return min + (float) (Math.random() * (max - min));
    }
}
