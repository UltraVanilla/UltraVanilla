package com.akoot.plugins.ultravanilla;

import com.akoot.plugins.ultravanilla.commands.*;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Ultravanilla extends JavaPlugin {

    public Ultravanilla instance;
    private YamlConfiguration nicknames;
    private YamlConfiguration colors;
    private YamlConfiguration bible;
    private Permission permissions;
    private List<String> swears;
    private List<String> swearsRaw;

    public List<String> getSwearsRaw() {
        return swearsRaw;
    }

    public YamlConfiguration getBible() {
        return bible;
    }

    public YamlConfiguration getNicknames() {
        return nicknames;
    }

    public YamlConfiguration getColors() {
        return colors;
    }

    public Permission getPermissions() {
        return permissions;
    }

    public List<String> getSwears() {
        return swears;
    }

    @Override
    public void onEnable() {
        instance = this;

        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        permissions = rsp.getProvider();
        if (permissions == null) {
            getLogger().warning("Vault could not link to a permissions provider.");
        }

        getDataFolder().mkdir();
        loadConfigs();

        getServer().getPluginManager().registerEvents(new EventListener(instance), instance);

        getCommand("nick").setExecutor(new NickCommand(instance));
        getCommand("suicide").setExecutor(new SuicideCommand(instance));
        getCommand("make").setExecutor(new MakeCommand(instance));
        getCommand("gamemode").setExecutor(new GamemodeCommand(instance));
        getCommand("title").setExecutor(new TitleCommand(instance));
    }

    public void loadConfigs() {
        getConfig("config.yml");
        nicknames = getConfig("nicknames.yml");
        colors = getConfig("colors.yml");
        bible = getConfig("swears.yml");

        swearsRaw = new ArrayList<>();
        swearsRaw.addAll(bible.getKeys(false));

        swears = new ArrayList<>();
        for (String string : bible.getKeys(false)) {
            String regex = "";
            for (char c : string.toCharArray()) {
                regex = regex + "(" + c + "|\\*).?";
            }
            swears.add(regex.substring(0, regex.length() - 2));
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

    public void saveNickname(UUID id, String nickname) {
        nicknames.set(id.toString(), nickname);
        saveConfig(nicknames, "nicknames.yml");
    }

    @Override
    public void onDisable() {
    }
}
