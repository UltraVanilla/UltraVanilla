package com.akoot.plugins.ultravanilla;

import com.akoot.plugins.ultravanilla.commands.*;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import com.akoot.plugins.ultravanilla.serializable.Position;
import com.akoot.plugins.ultravanilla.serializable.Powertool;
import com.akoot.plugins.ultravanilla.serializable.Title;
import com.akoot.plugins.ultravanilla.stuff.Channel;
import com.akoot.plugins.ultravanilla.stuff.Ticket;
import com.akoot.plugins.ultravanilla.util.RawMessage;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
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
import java.nio.file.Files;
import java.util.*;

public final class UltraVanilla extends JavaPlugin {

    public static UltraVanilla instance;
    private Permission permissions;
    private ProtocolManager protocolManager;

    private YamlConfiguration changelog;
    private YamlConfiguration storage;

    private List<Ticket> tickets;
    private String motd;

    private File configFile;

    public static YamlConfiguration getConfig(OfflinePlayer player) {
        return getConfig(player.getUniqueId());
    }

    private Random random;

    public static UltraVanilla getInstance() {
        return instance;
    }

    public static void tellRaw(RawMessage message, String name) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + name + " " + message.getJson());
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

    public static ChatColor getNameColor(OfflinePlayer player) {
        return ChatColor.valueOf(UltraVanilla.getConfig(player).getString("name-color", "RESET"));
    }

    public void loadConfigs() {
        init("join.txt", false);
        getConfig("config.yml", false);
        changelog = getConfig("changelog.yml", true);
        storage = getConfig("storage.yml", false);
    }

    private YamlConfiguration getConfig(String name, boolean overwrite) {
        YamlConfiguration config = new YamlConfiguration();
        File configFile = new File(this.getDataFolder(), name);
        init(name, overwrite);
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return config;
    }

    private void init(String name, boolean overwrite) {
        File file = new File(this.getDataFolder(), name);
        if (!file.exists() || overwrite) {
            InputStream fis = getClass().getResourceAsStream("/" + name);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
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

    public List<Ticket> getTickets() {
        return tickets;
    }

    public YamlConfiguration getStorage() {
        return storage;
    }

    public static boolean isSuperAdmin(Player player) {
        return getConfig(player.getUniqueId()).getBoolean("super-admin", false);
    }

    public static long getPlayTime(OfflinePlayer player) {
        long difference = player.getLastSeen() - player.getLastLogin();
        return UltraVanilla.getConfig(player).getLong("playtime", 0L) + difference;
    }

    public static void updatePlaytime(OfflinePlayer player) {
        UltraVanilla.set(player, "playtime", getPlayTime(player));
    }

    public void firstJoin(String name) throws IOException {
        File joinFile = new File(getDataFolder(), "join.txt");
        if (joinFile.exists()) {
            int count = 0;
            File bookFile = new File(getDataFolder(), "books.txt");
            if (bookFile.exists()) {
                count = Files.readAllBytes(bookFile.toPath())[0];
            } else {
                bookFile.createNewFile();
            }
            for (String line : Files.readAllLines(joinFile.toPath())) {
                line = line.replace("${player.name}", name);
                line = line.replace("${book.id}", String.format("%04d", count));
                getServer().dispatchCommand(Bukkit.getConsoleSender(), line);
            }
            Files.write(bookFile.toPath(), new byte[]{(byte) (count + 1)});
        }
    }

    public static void updateDisplayName(Player player) {
        YamlConfiguration config = UltraVanilla.getConfig(player);
        String displayName = config.getString("display-name");
        if (displayName != null) {
            displayName = ChatColor.valueOf(config.getString("name-color", "RESET")) + displayName;
        }
        player.setDisplayName(displayName);
        player.setPlayerListName((displayName != null ? displayName : player.getName()) + (Users.isAFK(player) ? " §7§o(AFK)" : ""));
    }

    public void ping(CommandSender player, Player target) {
        if (Users.isAFK(target)) {
            player.sendMessage(target.getName() + " is AFK");
        }
        target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.5F);
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    @Override
    public void onEnable() {
        instance = this;

        random = new Random();
        tickets = new ArrayList<>();

        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            getLogger().warning("Vault could not link to a permissions provider.");
        } else {
            permissions = rsp.getProvider();
        }

        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            getLogger().warning("Vault could not link to a permissions provider.");
        } else {
            protocolManager = ProtocolLibrary.getProtocolManager();
        }

        ConfigurationSerialization.registerClass(Position.class);
        ConfigurationSerialization.registerClass(Powertool.class);
        ConfigurationSerialization.registerClass(Title.class);

        getDataFolder().mkdir();
        Users.DIR.mkdir();
        loadConfigs();
        configFile = new File(getDataFolder(), "config.yml");

        getServer().getScheduler().scheduleSyncRepeatingTask(this, this::setRandomMOTD, 0L, 12 * 60 * 60 * 20L);

        for (Map<?, ?> map : storage.getMapList("tickets")) {
            Ticket ticket = new Ticket(this);
            ticket.load((Map<String, Object>) map);
            tickets.add(ticket);
        }

        getServer().getPluginManager().registerEvents(new EventListener(instance), instance);

        getCommand("nick").setExecutor(new NickCommand(instance));
        getCommand("suicide").setExecutor(new SuicideCommand(instance));
        getCommand("make").setExecutor(new MakeCommand(instance));
        getCommand("gm").setExecutor(new GmCommand(instance));
        getCommand("title").setExecutor(new TitleCommand(instance));
        getCommand("reload").setExecutor(new ReloadCommand(instance));
        getCommand("ping").setExecutor(new PingCommand(instance));
        getCommand("raw").setExecutor(new RawCommand(instance));
        getCommand("motd").setExecutor(new MotdCommand(instance));
        getCommand("ignore").setExecutor(new IgnoreCommand(instance));
        getCommand("home").setExecutor(new HomeCommand(instance));
        getCommand("seen").setExecutor(new SeenCommand(instance));
        getCommand("spawn").setExecutor(new SpawnCommand(instance));
        getCommand("print").setExecutor(new PrintCommand(instance));
        getCommand("do").setExecutor(new DoCommand(instance));
        getCommand("afk").setExecutor(new AfkCommand(instance));
        getCommand("msg").setExecutor(new MsgCommand(instance));
        getCommand("reply").setExecutor(new ReplyCommand(instance));
        getCommand("changelog").setExecutor(new ChangelogCommand(instance));
        getCommand("inventory").setExecutor(new InventoryCommand(instance));
        getCommand("lag").setExecutor(new LagCommand(instance));
        getCommand("ticket").setExecutor(new TicketCommand(instance));
        getCommand("customize").setExecutor(new CustomizeCommand(instance));
        getCommand("tptoggle").setExecutor(new TptoggleCommand(instance));
        getCommand("timezone").setExecutor(new TimezoneCommand(instance));
        getCommand("hat").setExecutor(new HatCommand(instance));
        getCommand("user").setExecutor(new UserCommand(instance));
        getCommand("smite").setExecutor(new SmiteCommand(instance));
        getCommand("back").setExecutor(new BackCommand(instance));
        getCommand("namecolor").setExecutor(new NameColorCommand(instance));
        getCommand("playtime").setExecutor(new PlayTimeCommand(instance));
        getCommand("whois").setExecutor(new WhoIsCommand(instance));
//        getCommand("votekick").setExecutor(new VoteKickCommand(instance));
    }

    private void setRandomMOTD() {
        List<String> motdList = getConfig().getStringList("motd.strings");
        setMOTD(motdList.get(random.nextInt(motdList.size())));
    }

    @Override
    public void onDisable() {
        saveStorage();
    }

    public String getTitle(String title, ChatColor color) {
        return getString("title", "{title}", title, "$color", color + "");
    }

    public String getString(String key, String... format) {
        String message = getConfig().getString(("strings." + key));
        for (int i = 0; i < format.length; i += 2) {
            message = message.replace(format[i], format[i + 1]);
        }
        return Palette.translate(message);
    }

    public String getString(String key) {
        return Palette.translate(getConfig().getString("strings." + key));
    }

    public Ticket getTicket(int id) {
        for (Ticket ticket : tickets) {
            if (ticket.getId() == id) {
                return ticket;
            }
        }
        return null;
    }

    public String getCommandString(String key) {
        return getConfig().getString("strings.command." + key);
    }

    public String getRandomString(String key, String... format) {
        List<String> list = getConfig().getStringList("strings." + key);
        String message = list.get(random.nextInt(list.size() - 1));
        for (int i = 0; i < format.length; i += 2) {
            message = message.replace(format[i], format[i + 1]);
        }
        return Palette.translate(message);
    }

    public void sendMessageToStaff(String message, String permission) {
        for (Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                player.sendMessage(message);
            }
        }
    }

    public void getChannels() {
        List<Map<?, ?>> maps = getStorage().getMapList("chat.channels");
        List<Channel> channels = new ArrayList<>();
        for (Map<?, ?> map : maps) {
            Channel channel = new Channel(map.get("id").toString());
        }
    }

    private void saveStorage() {

        // Tickets
        List<Map<?, ?>> ticketMaps = new ArrayList<>();
        for (Ticket ticket : tickets) {
            Map<String, Object> map = new HashMap<>();
            map.put("author", ticket.getAuthorId().toString());
            map.put("content", ticket.getContent());
            map.put("status", ticket.getStatus().toString());
            map.put("response", ticket.getReply());
            map.put("admin", ticket.getAdmin());
            ticketMaps.add(map);
        }
        storage.set("tickets", ticketMaps);

        saveConfig(storage, "storage.yml");
    }
}
