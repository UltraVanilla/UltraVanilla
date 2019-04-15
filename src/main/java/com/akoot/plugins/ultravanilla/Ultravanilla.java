package com.akoot.plugins.ultravanilla;

import com.akoot.plugins.ultravanilla.commands.*;
import com.akoot.plugins.ultravanilla.reference.Users;
import com.akoot.plugins.ultravanilla.util.RawMessage;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class Ultravanilla extends JavaPlugin {

    public static Ultravanilla instance;
    private YamlConfiguration swears;
    private Permission permissions;
    private List<String> swearsRegex;

    private Random random;
    private List<String> swearsRaw;

    public static Ultravanilla getInstance() {
        return instance;
    }

    public static void tellRaw(RawMessage message, String name) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + name + " " + message.getJSON());
    }

    public static void tellRaw(RawMessage message, Player player) {
        tellRaw(message, player.getName());
    }

    public static void tellRaw(RawMessage message) {
        tellRaw(message, "@a");
    }

    public static void set(Player player, String key, Object value) {
        set(player.getUniqueId(), key, value);
    }

    public static void set(OfflinePlayer player, String key, Object value) {
        set(player.getUniqueId(), key, value);
    }

    public static void set(UUID uid, String key, Object value) {
        YamlConfiguration config = getConfig(uid);
        if (config != null) {
            config.set(key, value);
            try {
                config.save(getUserFile(uid));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static File getUserFile(UUID uid) {
        File userFile = new File(Users.DIR, uid.toString() + ".yml");
        if (!userFile.exists()) {
            try {
                userFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userFile;
    }

    public static YamlConfiguration getConfig(UUID uid) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(getUserFile(uid));
        } catch (IOException | InvalidConfigurationException e) {
            return null;
        }
        return config;
    }

    public Random getRandom() {
        return random;
    }

    public List<String> getSwearsRaw() {
        return swearsRaw;
    }

    public YamlConfiguration getSwears() {
        return swears;
    }

    public Permission getPermissions() {
        return permissions;
    }

    public List<String> getSwearsRegex() {
        return swearsRegex;
    }

    @Override
    public void onEnable() {
        instance = this;

        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        permissions = rsp.getProvider();
        if (permissions == null) {
            getLogger().warning("Vault could not link to a permissions provider.");
        }

        random = new Random();

        getDataFolder().mkdir();
        Users.DIR.mkdir();
        loadConfigs();

        getServer().getPluginManager().registerEvents(new EventListener(instance), instance);

        getCommand("nick").setExecutor(new NickCommand(instance));
        getCommand("suicide").setExecutor(new SuicideCommand(instance));
        getCommand("make").setExecutor(new MakeCommand(instance));
        getCommand("gm").setExecutor(new GamemodeCommand(instance));
        getCommand("title").setExecutor(new TitleCommand(instance));
        getCommand("reloadconf").setExecutor(new ReloadCommand(instance));
        getCommand("config").setExecutor(new ConfigCommand(instance));
        getCommand("ping").setExecutor(new PingCommand(instance));
        getCommand("vote").setExecutor(new VoteCommand(instance));
        getCommand("raw").setExecutor(new RawCommand(instance));

    }

    public YamlConfiguration getEditableConfig(String name) {
        if (name.equalsIgnoreCase("swears")) {
            return swears;
        }
        return null;
    }

    public void loadConfigs() {
        getConfig("config.yml");
        swears = getConfig("swears.yml");

        swearsRaw = new ArrayList<>();
        swearsRaw.addAll(swears.getKeys(false));

        swearsRegex = new ArrayList<>();
        for (String string : swears.getKeys(false)) {
            String regex = "";
            for (char c : string.toCharArray()) {
                regex = regex + "(" + c + "|\\*)\\.?";
            }
            swearsRegex.add(regex.substring(0, regex.length() - 3));
        }
    }

    private YamlConfiguration getConfig(String name) {
        YamlConfiguration config = new YamlConfiguration();
        File configFile = new File(this.getDataFolder(), name);
        if (!configFile.exists()) {
            InputStream fis = getClass().getResourceAsStream("/" + name);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(configFile);
                byte[] buf = new byte[1024];
                int i = 0;
                while ((i = fis.read(buf)) != -1) {
                    fos.write(buf, 0, i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return config;
    }

    private void saveConfig(YamlConfiguration config, String fileName) {
        try {
            config.save(new File(getDataFolder(), fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ping(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.5F);
    }

    @Override
    public void onDisable() {
    }
}
