package world.ultravanilla.cache;

import world.ultravanilla.UltraVanilla;
import world.ultravanilla.cache.data.PlayerCacheEntry;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.OfflinePlayer;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class OfflinePlayerCacheManager {
    private final UltraVanilla plugin;
    private final Object2ObjectOpenHashMap<UUID, PlayerCacheEntry> playerCache = new Object2ObjectOpenHashMap<>();
    private final File cacheFile;
    private long lastLoaded = 0L;

    public OfflinePlayerCacheManager(UltraVanilla instance) {
        this.plugin = instance;
        this.cacheFile = new File(plugin.getDataFolder(), "offline-players.ser");
    }

    public Object2ObjectOpenHashMap<UUID, PlayerCacheEntry> getOfflinePlayers() {
        if (playerCache.isEmpty()) {
            try {
                loadCache();
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to load offline player cache", e);
            }
        }
        
        lastLoaded = System.currentTimeMillis();
        File playerDataFolder = new File(plugin.getServer().getWorlds().get(0).getWorldFolder(), "playerdata");
        File[] files = playerDataFolder.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String uuidPart = fileName.substring(0, fileName.length() - 4);
                try {
                    UUID uuid = UUID.fromString(uuidPart);
                    long lastModified = file.lastModified();
                    PlayerCacheEntry entry = playerCache.get(uuid);
                    if (entry == null || entry.lastModified != lastModified) {
                        String name = plugin.getServer().getOfflinePlayer(uuid).getName();
                        playerCache.put(uuid, new PlayerCacheEntry(uuid, name, lastModified));
                    }
                } catch (IllegalArgumentException ignored) {
                    // Ignore files that don't match UUID format
                }
            }
        }
    
        return playerCache;
    }

    public List<UUID> getAllPlayerUUIDs() {
        List<UUID> uuids = new ArrayList<>();
        File playerDataFolder = new File(plugin.getServer().getWorlds().get(0).getWorldFolder(), "playerdata");
        File[] files = playerDataFolder.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                String uuidPart = name.substring(0, name.length() - 4); // Remove ".dat"
                try {
                    uuids.add(UUID.fromString(uuidPart));
                } catch (IllegalArgumentException ignored) {
                    // Ignore files that don't match UUID format
                }
            }
        }
        return uuids;
    }

    public void saveCache() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile))) {
            oos.writeObject(playerCache);
        }
    }

    private void loadCache() throws IOException, ClassNotFoundException {
        if (!cacheFile.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile))) {
            Object obj = ois.readObject();
            if (obj instanceof Object2ObjectOpenHashMap) {
                playerCache.clear();
                playerCache.putAll((Object2ObjectOpenHashMap<UUID, PlayerCacheEntry>) obj);
            }
        }
    }
}