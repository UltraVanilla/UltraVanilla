package com.akoot.plugins.ultravanilla;

import com.akoot.plugins.ultravanilla.commands.*;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import com.akoot.plugins.ultravanilla.serializable.Position;
import com.akoot.plugins.ultravanilla.serializable.Powertool;
import com.akoot.plugins.ultravanilla.stuff.Poll;
import com.akoot.plugins.ultravanilla.util.RawMessage;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
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
    private Permission permissions;

    private YamlConfiguration changelog;

    private List<Poll> polls;
    private List<String> motds;
    private String motd;

    public List<Poll> getPolls() {
        return polls;
    }

    public List<String> getMotds() {
        return motds;
    }
    private Random random;

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

    public String getMOTD() {
        return motd;
    }

    public YamlConfiguration getChangelog() {
        return changelog;
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

    public Permission getPermissions() {
        return permissions;
    }

    public void setMOTD(String motd) {
        this.motd = Palette.translate(motd);
    }

    public static boolean isIgnored(Player player, Player target) {
        List<String> ignored = getConfig(player.getUniqueId()).getStringList(Users.IGNORED);
        return ignored.contains(target.getUniqueId().toString());
    }

    public void loadConfigs() {
        getConfig("config.yml", false);
        changelog = getConfig("changelog.yml", true);
    }

    private YamlConfiguration getConfig(String name, boolean overwrite) {
        YamlConfiguration config = new YamlConfiguration();
        File configFile = new File(this.getDataFolder(), name);
        if (!configFile.exists() || overwrite) {
            InputStream fis = getClass().getResourceAsStream("/" + name);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(configFile);
                byte[] buf = new byte[1024];
                int i;
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

    public static void add(UUID uid, String key, String value) {
        List<String> list = getConfig(uid).getStringList(key);
        list.add(value);
        set(uid, key, list);
    }

    public static void remove(UUID uid, String key, String value) {
        List<String> list = getConfig(uid).getStringList(key);
        list.remove(value);
        set(uid, key, list);
    }

    @Override
    public void onEnable() {
        instance = this;

        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            getLogger().warning("Vault could not link to a permissions provider.");
        } else {
            permissions = rsp.getProvider();
        }

        random = new Random();
        polls = new ArrayList<>();

        ConfigurationSerialization.registerClass(Position.class);
        ConfigurationSerialization.registerClass(Powertool.class);

        getDataFolder().mkdir();
        Users.DIR.mkdir();
        loadConfigs();

        motds = getConfig().getStringList("motd.strings");
        getServer().getScheduler().scheduleSyncRepeatingTask(this, this::setRandomMOTD, 0L, 12 * 60 * 60 * 20L);

        getServer().getPluginManager().registerEvents(new EventListener(instance), instance);

        getCommand("nick").setExecutor(new NickCommand(instance));
        getCommand("suicide").setExecutor(new SuicideCommand(instance));
        getCommand("make").setExecutor(new MakeCommand(instance));
        getCommand("gm").setExecutor(new GamemodeCommand(instance));
        getCommand("title").setExecutor(new TitleCommand(instance));
        getCommand("reloadconf").setExecutor(new ReloadCommand(instance));
        getCommand("ping").setExecutor(new PingCommand(instance));
        getCommand("vote").setExecutor(new VoteCommand(instance));
        getCommand("raw").setExecutor(new RawCommand(instance));
        getCommand("motd").setExecutor(new MotdCommand(instance));
        getCommand("ignore").setExecutor(new IgnoreCommand(instance));
        getCommand("home").setExecutor(new HomeCommand(instance));
        getCommand("seen").setExecutor(new SeenCommand(instance));
        getCommand("spawn").setExecutor(new SpawnCommand(instance));
        getCommand("print").setExecutor(new PrintCommand(instance));
        getCommand("user").setExecutor(new UserCommand(instance));
        getCommand("do").setExecutor(new DoCommand(instance));
        getCommand("afk").setExecutor(new AfkCommand(instance));
        getCommand("msg").setExecutor(new MsgCommand(instance));
        getCommand("reply").setExecutor(new ReplyCommand(instance));
        getCommand("changelog").setExecutor(new ChangelogCommand(instance));
        getCommand("inventory").setExecutor(new InventoryCommand(instance));
    }

    private void setRandomMOTD() {
        setMOTD(motds.get(random.nextInt(motds.size())));
    }

    public void ping(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.5F);
    }

    @Override
    public void onDisable() {
    }
}
