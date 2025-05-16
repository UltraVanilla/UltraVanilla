package world.ultravanilla.cache.data;

import java.io.Serializable;
import java.util.UUID;

public class PlayerCacheEntry implements Serializable {
    public final UUID uuid;
    public String name;
    public long lastModified;

    public PlayerCacheEntry(UUID uuid, String name, long lastModified) {
        this.uuid = uuid;
        this.name = name;
        this.lastModified = lastModified;
    }
}