package com.akoot.plugins.ultravanilla;

import com.akoot.plugins.ultravanilla.commands.*;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import com.akoot.plugins.ultravanilla.serializable.LoreItem;
import com.akoot.plugins.ultravanilla.serializable.Position;
import com.akoot.plugins.ultravanilla.serializable.Powertool;
import com.akoot.plugins.ultravanilla.serializable.Title;
import com.akoot.plugins.ultravanilla.stuff.Range;
import com.akoot.plugins.ultravanilla.stuff.Ticket;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
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
    private Permission vault;
    private LuckPerms luckPerms;

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    private YamlConfiguration changelog;
    private YamlConfiguration storage;

    private List<Ticket> tickets;
    private String motd;

    private File configFile;

    public static YamlConfiguration getConfig(OfflinePlayer player) {
        return getConfig(player.getUniqueId());
    }

    private Random random;

    private StaffActionsRecord staffActionsRecord;

    public static UltraVanilla getInstance() {
        return instance;
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

    public String getRoleCapitalized(String role) {
        return getConfig().getString("rename-groups." + role, role.substring(0, 1).toUpperCase() + role.substring(1));
    }

    public String getColoredRole(String role) {
        return ChatColor.of(getConfig().getString("color.rank." + role, "#ffffff")) + getRoleCapitalized(role);
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

    public Permission getVault() {
        return vault;
    }

    public void setMOTD(String motd) {
        this.motd = Palette.translate(motd);
    }

    public static boolean isIgnored(Player player, Player target) {
        List<String> ignored = getConfig(player.getUniqueId()).getStringList(Users.IGNORED);
        return ignored.contains(target.getUniqueId().toString());
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

    public void ping(Player target) {
        target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.5F);
    }

    public void ping(CommandSender sender, Player target) {
        if (Users.isAFK(target)) {
            sender.sendMessage(target.getName() + " is AFK");
        }
        ping(target);
    }

    public StaffActionsRecord getStaffActionsRecord() {
        return staffActionsRecord;
    }

    public static boolean isSafeLocation(Location location) {
        try {
            Block feet = location.getBlock();
            if (!feet.getType().isTransparent() && !feet.getLocation().add(0, 1, 0).getBlock().getType().isTransparent()) {
                return false; // not transparent (will suffocate)
            }
            Block head = feet.getRelative(BlockFace.UP);
            if (!head.getType().isTransparent()) {
                return false; // not transparent (will suffocate)
            }
            Block ground = feet.getRelative(BlockFace.DOWN);
            // returns if the ground is solid or not.
            return ground.getType().isSolid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isStaff(CommandSender sender) {
        return sender.hasPermission("ultravanilla.permission.staff");
    }

    public void loadConfig(YamlConfiguration config, String file) {
        File configFile = new File(getDataFolder(), file);
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
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

    public void removeFromStorage(String key, String value) {
        List<String> list = storage.getStringList(key);
        list.remove(value);
        storage.set(key, list);
    }

    public void addToStorage(String key, String value) {
        List<String> list = storage.getStringList(key);
        list.add(value);
        storage.set(key, list);
    }

    public void store(String key, Object value) {
        storage.set(key, value);
        saveConfig(storage, "storage.yml");
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

    public String getNextRole(OfflinePlayer player) {
        String[] roles = getAllTimedRoles();
        for (int i = 0; i < roles.length - 1; i++) {
            String role = roles[i];
            String nextRole = roles[i + 1];
            if (getRole(player).equals(role)) {
                return nextRole;
            }
        }
        return roles[0];
    }

    public long getNextRoleDate(OfflinePlayer player) {
        return player.getFirstPlayed() + getConfig().getLong("times." + getNextRole(player));
    }

    public String getRole(OfflinePlayer player) {
        if (player.isOnline()) {
            return getVault().getPrimaryGroup((Player) player);
        } else {
            return getVault().getPrimaryGroup(null, player);
        }
    }

    public void async(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(this, task);
    }

    public ChatColor getRoleColor(String group) {
        return ChatColor.of(getConfig().getString("color.rank." + group, "RESET"));
    }

    public String[] getAllTimedRoles() {
        return ((MemorySection) getConfig().get("times")).getKeys(false).toArray(new String[0]);
    }

    public boolean hasRightRole(OfflinePlayer player) {
        return getRole(player).equals(getRoleShouldHave(player));
    }

    public String getRoleShouldHave(OfflinePlayer player) {
        String[] roles = getAllTimedRoles();
        String role = roles[0];
        long timePlayed = System.currentTimeMillis() - player.getFirstPlayed();
        for (String key : roles) {
            long roleTime = getConfig().getLong("times." + key);
            if (timePlayed >= roleTime) {
                role = key;
            }
        }
        return role;
    }

    public static boolean isAdmin(CommandSender sender) {
        return sender.hasPermission("ultravanilla.permission.admin");
    }

    @Override
    public void onEnable() {

        instance = this;

        random = new Random();
        staffActionsRecord = new StaffActionsRecord(this);
        tickets = new ArrayList<>();

        // Vault API
        RegisteredServiceProvider<Permission> vaultProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if (vaultProvider == null) {
            getLogger().warning("Could not link to Vault.");
        } else {
            vault = vaultProvider.getProvider();
        }

        // Add luckperms API
        RegisteredServiceProvider<LuckPerms> luckPermsProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (luckPermsProvider == null) {
            getLogger().warning("Could not link to LuckPerms.");
        } else {
            luckPerms = luckPermsProvider.getProvider();
        }

        ConfigurationSerialization.registerClass(Position.class);
        ConfigurationSerialization.registerClass(Powertool.class);
        ConfigurationSerialization.registerClass(Title.class);
        ConfigurationSerialization.registerClass(LoreItem.class, "LoreItem");

        getDataFolder().mkdir();
        Users.DIR.mkdir();
        loadConfigs();
        configFile = new File(getDataFolder(), "config.yml");

        getServer().getScheduler().scheduleSyncRepeatingTask(this, this::setRandomMOTD, 0L, 12 * 60 * 60 * 20L);

        loadConfig(storage, "storage.yml");
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

        SeenCommand seenCommand = new SeenCommand(instance);
        getCommand("seen").setExecutor(seenCommand);
        getCommand("firstjoined").setExecutor(seenCommand);
        getCommand("lastseen").setExecutor(seenCommand);

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

        CustomizeCommand customizeCommand = new CustomizeCommand(instance);
        getCommand("customize").setExecutor(customizeCommand);
        getCommand("rename").setExecutor(customizeCommand);
        getCommand("setlore").setExecutor(customizeCommand);

        getCommand("tptoggle").setExecutor(new TptoggleCommand(instance));
        getCommand("timezone").setExecutor(new TimezoneCommand(instance));
        getCommand("hat").setExecutor(new HatCommand(instance));
        getCommand("user").setExecutor(new UserCommand(instance));
        getCommand("smite").setExecutor(new SmiteCommand(instance));
        getCommand("back").setExecutor(new BackCommand(instance));
        getCommand("namecolor").setExecutor(new NameColorCommand(instance));
        getCommand("playtime").setExecutor(new PlayTimeCommand(instance));
        getCommand("whois").setExecutor(new WhoIsCommand(instance));

        MuteCommand muteCommand = new MuteCommand(instance);
        getCommand("mute").setExecutor(muteCommand);
        getCommand("smute").setExecutor(muteCommand);
        getCommand("unmute").setExecutor(muteCommand);
        getCommand("sunmute").setExecutor(muteCommand);

        getCommand("mcolor").setExecutor(new McolorCommand(instance));

        SignCommand signCommand = new SignCommand(instance);
        getCommand("sign").setExecutor(signCommand);
        getServer().getPluginManager().registerEvents(signCommand, instance);

        getCommand("promote").setExecutor(new PromoteCommand(instance));

        // New admin command suite
        getCommand("tempban").setExecutor(new TempBanCommand(instance));
        getCommand("ban").setExecutor(new BanCommand(instance));
        getCommand("ban-ip").setExecutor(new BanIpCommand(instance));
        getCommand("kick").setExecutor(new KickCommand(instance));
        getCommand("permaban").setExecutor(new PermabanCommand(instance));
        getCommand("pardon").setExecutor(new PardonCommand(instance));
        getCommand("warn").setExecutor(new WarnCommand(instance));
        getCommand("pardon-ip").setExecutor(new PardonIpCommand(instance));

        getCommand("rtp").setExecutor(new RtpCommand(instance));

        getCommand("setgroup").setExecutor(new SetGroupCommand(instance));


        // Scrapped until further notice
//        MailCommand mailCommand = new MailCommand(instance);
//        getCommand("mail").setExecutor(mailCommand);
//        getServer().getPluginManager().registerEvents(mailCommand, instance);

    }

    // https://github.com/LuckPerms/api-cookbook/blob/master/src/main/java/me/lucko/lpcookbook/commands/SetGroupCommand.java
    public void setGroup(OfflinePlayer player, String group) {
        luckPerms.getUserManager().modifyUser(player.getUniqueId(), (User user) -> {

            // Remove all other inherited groups the user had before.
            user.data().clear(NodeType.INHERITANCE::matches);
            user.data().remove(InheritanceNode.builder("default").build());

            // Create a node to add to the player.
            Node node = InheritanceNode.builder(group).build();

            // Add the node to the user.
            user.data().add(node);
        });
    }

    public Player getRandomOnlinePlayer() {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        return new ArrayList<Player>(players).get((int) new Range(players.size()).getRandom());
    }

    public static void killPlayer(Player player, String message) {
        set(player.getUniqueId(), "death-message", message);
        player.setHealth(0);
    }
}
