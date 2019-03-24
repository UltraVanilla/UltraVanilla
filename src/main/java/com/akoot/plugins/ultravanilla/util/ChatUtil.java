package com.akoot.plugins.ultravanilla.util;

import java.util.Random;

public class ChatUtil {

    private static Random random = new Random();

    public static String censor(String msg) {
        String censored = "";
        char[] replacer = {'!', '@', '#', '$', '%', '%', '^', '&', '*', '='};
        int last = -1;
        int r = last;
        for (int i = 0; i < msg.length(); i++) {
            r = random.nextInt(replacer.length - 1);
            while (r == last)
                r = random.nextInt(replacer.length - 1);
            censored += replacer[r];
            last = r;
        }
        return censored;
    }
}
