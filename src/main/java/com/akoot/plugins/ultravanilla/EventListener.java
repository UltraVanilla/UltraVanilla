package com.akoot.plugins.ultravanilla;

import com.akoot.plugins.ultravanilla.commands.AfkCommand;
import com.akoot.plugins.ultravanilla.commands.MuteCommand;
import com.akoot.plugins.ultravanilla.commands.SuicideCommand;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import com.akoot.plugins.ultravanilla.serializable.LoreItem;
import com.akoot.plugins.ultravanilla.serializable.Position;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventListener implements Listener {

    private final UltraVanilla plugin;

    public EventListener(UltraVanilla instance) {
        this.plugin = instance;
    }

    private void unsetAfk(Player player) {
        if (Users.isAFK(player)) {
            Users.AFK.remove(player.getUniqueId());
            plugin.getServer().broadcastMessage(player.getDisplayName() + AfkCommand.COLOR + " is no longer AFK");
            player.setPlayerListName(player.getDisplayName());
        }
    }

    // Might break with future versions
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!UltraVanilla.isSuperAdmin(event.getPlayer())) {
            if (event.getCause().name().equals("COMMAND") || event.getCause().name().equals("SPECTATE")) {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (p.getLocation().equals(event.getTo())) {
                        YamlConfiguration config = UltraVanilla.getConfig(p.getUniqueId());
                        if (config != null && config.getBoolean(Users.TP_DISABLED, false)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
        UltraVanilla.set(event.getPlayer(), "last-teleport", new Position(event.getFrom()));
    }

    @EventHandler
    public void onCommandType(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage();
        String newMessage = message;

        // ${}
        Pattern pattern = Pattern.compile("\\$\\{([\\w]+)\\.([\\w-.]+)}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            newMessage = message.replaceAll(Pattern.quote(matcher.group()),
                    getValue(matcher.group(1), matcher.group(2).toLowerCase().split("\\.")));
        }

        // --as
        if (newMessage.contains("--as ")) {
            String command = newMessage.substring(1, newMessage.indexOf("--as ") - 1);
            String target = newMessage.substring(newMessage.indexOf("--as ") + "--as ".length());
            newMessage = "/make " + target + " do " + command;
        }

        // --at
        if (newMessage.contains("--at ")) {
            String command = newMessage.substring(1, newMessage.indexOf("--at ") - 1);
            String location = newMessage.substring(newMessage.indexOf("--at ") + "--at ".length());
            newMessage = "/execute at " + location + " run " + command;
        }

        // --in
        if (newMessage.contains("--in ")) {
            String command = newMessage.substring(1, newMessage.indexOf("--in ") - 1);
            String world = newMessage.substring(newMessage.indexOf("--in ") + "--in ".length());
            newMessage = "/execute in " + world + " run " + command;
        }

        e.setMessage(newMessage);
    }

    public String getValue(String parent, String[] children) {
        if (parent.equals("player")) {
            if (children.length == 2) {
                OfflinePlayer player = plugin.getServer().getOfflinePlayer(children[0]);
                if (player.hasPlayedBefore() || player.isOnline()) {
                    if (children[1].matches("(nick|display|custom)[-_]?name")) {
                        if (UltraVanilla.getConfig(player).contains("display-name")) {
                            return UltraVanilla.getConfig(player).getString("display-name");
                        }
                    } else if (children[1].matches("role|rank|group")) {
                        return plugin.getRole(player);
                    } else if (children[1].matches("next[_-]?(role|rank|group)")) {
                        return plugin.getNextRole(player);
                    } else if (children[1].equals("uuid")) {
                        return player.getUniqueId().toString();
                    }
                }
            }
        } else if (parent.equals("color")) {
            if (children.length == 2) {
                String theClass = children[0];
                if (theClass.matches("rank|role|group")) {
                    ChatColor roleColor = plugin.getRoleColor(children[1]);
                    if (roleColor != null) {
                        return Palette.untranslate(roleColor + "");
                    }
                }
            } else if (children.length == 1) {
                ChatColor color = ChatColor.of(children[0]);
                if (color != null) {
                    return Palette.untranslate(color + "");
                }
            }
        }
        return parent + "." + String.join(".", children);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        unsetAfk(player);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        String version = plugin.getServer().getVersion();
        version = version.substring(version.indexOf("MC: ") + 4, version.length() - 1);
        event.setMotd(Palette.translate(plugin.getConfig().getString("motd.server-name")) +
                " " + ChatColor.of(plugin.getConfig().getString("motd.version-color")) + version +
                "\n" + ChatColor.RESET + plugin.getMOTD());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String nick = UltraVanilla.getConfig(player.getUniqueId()).getString("display-name");
        if (nick != null) {
            UltraVanilla.updateDisplayName(player);
            event.setJoinMessage(event.getJoinMessage().replace(player.getName(), player.getDisplayName() + ChatColor.YELLOW));
        }
        if (!player.hasPlayedBefore()) {
            Position spawn = ((Position) plugin.getConfig().get("spawn"));
            if (spawn != null) {
                player.teleport(spawn.getLocation());
            }
            UltraVanilla.set(player, Users.FIRST_LOGIN, System.currentTimeMillis());
            try {
                plugin.firstJoin(player.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String thisVersion = plugin.getDescription().getVersion();
        String lastVersion = UltraVanilla.getConfig(player.getUniqueId()).getString("last-version");
        if (lastVersion == null || !lastVersion.equals(thisVersion) && player.hasPlayedBefore()) {
            player.performCommand("changelog");
        }
        UltraVanilla.set(player, "last-version", thisVersion);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UltraVanilla.set(player.getUniqueId(), Users.LOGOUT_LOCATION, new Position(event.getPlayer().getLocation()));

        UltraVanilla.updatePlaytime(player);

        event.setQuitMessage(event.getQuitMessage().replace(player.getName(), player.getDisplayName() + ChatColor.YELLOW));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.hasPermission("ultravanilla.command.suicide")) {
            String message = event.getDeathMessage();
            if (message != null && message.endsWith(" died")) {
                message = plugin.getRandomString("suicide-messages", "{player}", player.getDisplayName(), "$color", SuicideCommand.COLOR + "");
                event.setDeathMessage(message);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        List<LoreItem> loreItems = (List<LoreItem>) plugin.getStorage().getList("lore-items");
        if (block.getState().getType() == Material.PLAYER_HEAD) {
            if (loreItems != null && !loreItems.isEmpty()) {
                for (LoreItem loreItem : loreItems) {
                    if (loreItem != null && loreItem.getPosition().equals(location)) {
                        event.setDropItems(false);
                        ItemStack itemStack = block.getDrops().iterator().next();
                        ItemMeta meta = itemStack.getItemMeta();

                        String name = loreItem.getName();
                        if (!name.isEmpty()) meta.setDisplayName(name);

                        List<String> lore = loreItem.getLore();
                        if (lore != null) meta.setLore(lore);

                        itemStack.setItemMeta(meta);
                        block.getWorld().dropItem(event.getBlock().getLocation(), itemStack);
                        loreItems.remove(loreItem);
                        plugin.store("lore-items", loreItems);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemMeta meta = event.getItemInHand().getItemMeta();

        if (event.getItemInHand().getType() == Material.PLAYER_HEAD) {
            if (meta != null && !(meta.getDisplayName().isEmpty() && (meta.getLore() != null && meta.getLore().isEmpty()))) {
                LoreItem item = new LoreItem(meta.getDisplayName(), meta.getLore(), new Position(event.getBlockPlaced().getLocation()));
                List<LoreItem> loreItems = (List<LoreItem>) plugin.getStorage().getList("lore-items");
                if (loreItems == null) {
                    loreItems = new ArrayList<>();
                }
                loreItems.add(item);
                plugin.store("lore-items", loreItems);
            }
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        unsetAfk(player);
        YamlConfiguration config = UltraVanilla.getConfig(player.getUniqueId());
        String message = event.getMessage();

        // Mute
        if (config.getBoolean("muted", false)) {
            player.sendMessage(MuteCommand.COLOR + "You are muted.");
            event.setCancelled(true);
            plugin.getLogger().info(player.getName() + " tried to say: " + message);
            return;
        }

        // Chat color
        if (player.hasPermission("ultravanilla.chat.color")) {
            message = Palette.translate(message);
        }

        // Pings
        if (message.contains("@")) {
            Pattern p = Pattern.compile("@([a-zA-Z0-9_]{2,})");
            Matcher m = p.matcher(message);
            while (m.find()) {
                String match = m.group(0);
                String name = m.group(1);
                if (name.equals("everyone") && player.hasPermission("ultravanilla.chat.everyone")) {
                    for (Player recipient : event.getRecipients()) {
                        message = message.replace(match, ChatColor.of(plugin.getConfig().getString("color.chat.ping.everyone")) + match + ChatColor.RESET);
                        plugin.ping(recipient);
                    }
                } else {
                    for (Player recipient : event.getRecipients()) {
                        if (recipient.getName().toLowerCase().contains(name.toLowerCase()) || ChatColor.stripColor(recipient.getDisplayName()).toLowerCase().contains(name)) {
                            message = message.replace(match, ChatColor.of(plugin.getConfig().getString("color.chat.ping.user")) + name + ChatColor.RESET);
                            plugin.ping(player, recipient);
                        }
                    }
                }
            }
        }

        //ignored
        for (Player p : event.getRecipients()) {
            if (UltraVanilla.isIgnored(p, player)) {
                event.getRecipients().remove(p);
                break;
            }
        }

        event.setMessage(message);

        // Chat formatter
        boolean donator = player.hasPermission("ultravanilla.donator");
        boolean staff = player.hasPermission("ultravanilla.staff-custom");
        String textPrefix = config.getString("text-prefix", ChatColor.RESET + "");
        String group = plugin.getVault().getPrimaryGroup(player);
        String rankColor = plugin.getRoleColor(group) + "";
        String rank = plugin.getConfig().getString("rename-groups." + group, group.substring(0, 1).toUpperCase() + group.substring(1));

        String donatorBracketsColor = ChatColor.of(plugin.getConfig().getString("color.chat.brackets.donator")) + "";
        String rankBracketsColor = ChatColor.of(plugin.getConfig().getString("color.chat.brackets.rank")) + "";
        String nameBracketsColor = ChatColor.of(plugin.getConfig().getString("color.chat.brackets.name")) + "";
        String defaultNameColor = ChatColor.of(plugin.getConfig().getString("color.chat.default-name-color")) + "";
        String staffColor = ChatColor.of(plugin.getConfig().getString("color.rank.staff")) + "";

        String format = String.format("%s%s%s[%s%s%s] %s<%s%s> %s%s",
                (donator ?
                        String.format("%s[%sD%s] ",
                                donatorBracketsColor, ChatColor.of(plugin.getConfig().getString("color.rank.donator")), donatorBracketsColor)
                        : ""),
                (staff ?
                        String.format("%s[%sStaff%s] ", rankBracketsColor, staffColor, rankBracketsColor)
                        : ""),
                rankBracketsColor, rankColor, rank, rankBracketsColor,
                nameBracketsColor, defaultNameColor + "%1$s", nameBracketsColor,
                textPrefix, "%2$s");

        String formatted = Palette.translate(format);
        event.setFormat(formatted);
    }
}
