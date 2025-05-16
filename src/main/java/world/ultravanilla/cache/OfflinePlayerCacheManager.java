package world.ultravanilla.cache;

import world.ultravanilla.UltraVanilla;
import world.ultravanilla.cache.data.PlayerCacheEntry;

import org.bukkit.Bukkit; // <-- Needed for Bukkit.isPrimaryThread() and Bukkit.getScheduler()

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.HashMap;

public class OfflinePlayerCacheManager {
    private final UltraVanilla plugin;
    private final HashMap<UUID, PlayerCacheEntry> playerCache = new HashMap<>();
    private final File cacheFile;
    private long lastLoaded = 0L;

    public OfflinePlayerCacheManager(UltraVanilla instance) {
        this.plugin = instance;
        this.cacheFile = new File(plugin.getDataFolder(), "offline-players.json");
    }

    public HashMap<UUID, PlayerCacheEntry> getOfflinePlayers() {
        synchronized (playerCache) {
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
                            String name = getPlayerNameSync(uuid);
                            playerCache.put(uuid, new PlayerCacheEntry(uuid, name, lastModified));
                        }
                    } catch (IllegalArgumentException ignored) {
                        // Ignore files that don't match UUID format
                    }
                }
            }
            return new HashMap<>(playerCache);
        }
    }

    public List<UUID> getAllPlayerUUIDs() {
        synchronized (playerCache) {
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
    }

    public void saveCache() throws IOException {
            Gson gson = new Gson();
            synchronized (playerCache) {
                try (FileWriter writer = new FileWriter(cacheFile)) {
                    gson.toJson(new HashMap<>(playerCache), writer);
                }
            }
        }

    public void loadCache() throws IOException {
        if (!cacheFile.exists()) return;
        Gson gson = new Gson();
        synchronized (playerCache) {
            try (FileReader reader = new FileReader(cacheFile)) {
                Type type = new TypeToken<HashMap<UUID, PlayerCacheEntry>>(){}.getType();
                HashMap<UUID, PlayerCacheEntry> loaded = gson.fromJson(reader, type);
                if (loaded != null) {
                    playerCache.clear();
                    playerCache.putAll(loaded);
                }
            }
        }
    }

    private String getPlayerNameSync(UUID uuid) {
        if (Bukkit.isPrimaryThread()) {
            return plugin.getServer().getOfflinePlayer(uuid).getName();
        } else {
            try {
                Future<String> future = Bukkit.getScheduler().callSyncMethod(plugin, () ->
                    plugin.getServer().getOfflinePlayer(uuid).getName()
                );
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to fetch player name for " + uuid, e);
                return null;
            }
        }
    }
}