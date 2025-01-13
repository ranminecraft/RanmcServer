package cc.ranmc.server.util;

import java.util.Random;

public class KeyGenerator {

    public static String get() {
        StringBuilder builder = new StringBuilder();
        double length = (Math.random() * 3) + 4;
        for (int i = 0; i < length; i++) {
            String content = "abcdefghijklmnopqrstuvwxyz0123456789";
            int number = new Random().nextInt(content.length() - 1);
            builder.append(content.charAt(number));
        }
        return builder.toString();
    }
}
