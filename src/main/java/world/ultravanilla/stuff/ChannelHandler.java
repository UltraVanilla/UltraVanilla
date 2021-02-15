package world.ultravanilla.stuff;

import java.util.HashMap;
import java.util.Map;

public class ChannelHandler {

    private static final Map<String, String> channels = new HashMap<>();

    public static Map<String, String> getChannels() {
        return channels;
    }
}
