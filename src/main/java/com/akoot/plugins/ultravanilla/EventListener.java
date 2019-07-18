package com.akoot.plugins.ultravanilla;

import com.akoot.plugins.ultravanilla.commands.AfkCommand;
import com.akoot.plugins.ultravanilla.commands.SuicideCommand;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import com.akoot.plugins.ultravanilla.serializable.Position;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;

public class EventListener implements Listener {

    private UltraVanilla plugin;

    public EventListener(UltraVanilla instance) {
        this.plugin = instance;
    }

    private void unsetAfk(Player player) {
        if (Users.isAFK(player)) {
            Users.AFK.remove(player.getUniqueId());
            plugin.getServer().broadcastMessage(Palette.translate(plugin.getCommandString("afk.message.false")
                    .replace("{player}", player.getName())
                    .replace("$color", AfkCommand.COLOR + "")
            ));
        }
    }

    // Might break with future versions
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!UltraVanilla.isSuperAdmin(event.getPlayer())) {
            if (event.getCause().name().equals("COMMAND") || event.getCause().name().equals("SPECTATE")) {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (p.getLocation().equals(event.getTo())) {
                        YamlConfiguration config = plugin.getConfig(p.getUniqueId());
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
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        unsetAfk(player);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        OfflinePlayer[] offlinePlayers = plugin.getServer().getOfflinePlayers();
        OfflinePlayer offlinePlayer;
        String name = "Nobody";
        if (offlinePlayers.length > 0) {
            offlinePlayer = offlinePlayers[plugin.getRandom().nextInt(offlinePlayers.length)];
            name = offlinePlayer.getName();
        }
        String version = plugin.getServer().getVersion();
        version = version.substring(version.indexOf("MC: ") + 4, version.length() - 1);
        event.setMotd(Palette.translate(plugin.getConfig().getString("motd.server-name")) +
                " " + ChatColor.valueOf(plugin.getConfig().getString("motd.version-color")) + version +
                "\n" + ChatColor.RESET + plugin.getMOTD().replace("%player", name));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String nick = UltraVanilla.getConfig(player.getUniqueId()).getString(Users.NICKNAME);
        String nameColor = ChatColor.valueOf(UltraVanilla.getConfig(player.getUniqueId()).getString("name-color", "RESET")) + "";
        if (nick != null) {
            player.setDisplayName(nick + ChatColor.RESET);
            player.setPlayerListName(nick);
            event.setJoinMessage(event.getJoinMessage().replace(player.getName(), ChatColor.stripColor(nick)));
            player.setCustomName(ChatColor.stripColor(nick));
            player.setCustomNameVisible(true);
        }
        player.setPlayerListName(nameColor + player.getPlayerListName());
        UltraVanilla.set(player, Users.LAST_LOGIN, System.currentTimeMillis());
        if (!player.hasPlayedBefore()) {
            Position spawn = ((Position) plugin.getConfig().get("spawn"));
            if (spawn != null) {
                player.teleport(spawn.getLocation());
            }
            UltraVanilla.set(player, Users.FIRST_LOGIN, System.currentTimeMillis());
            plugin.firstJoin(player.getName());
        }
        String thisVersion = plugin.getDescription().getVersion();
        String lastVersion = UltraVanilla.getConfig(player.getUniqueId()).getString("last-version");
        if (lastVersion == null || !lastVersion.equals(thisVersion)) {
            player.performCommand("changelog");
        }
        UltraVanilla.set(player, "last-version", thisVersion);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UltraVanilla.set(player.getUniqueId(), Users.LOGOUT_LOCATION, new Position(event.getPlayer().getLocation()));
        UltraVanilla.set(player.getUniqueId(), Users.LAST_LOGOUT, System.currentTimeMillis());
        event.setQuitMessage(event.getQuitMessage().replace(player.getName(), ChatColor.stripColor(player.getDisplayName())));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.hasPermission("ultravanilla.command.suicide")) {
            String message = event.getDeathMessage();
            if (message != null && message.endsWith(" died")) {
                message = plugin.getRandomString("suicide-messages", "{player}", player.getName(), "$color", SuicideCommand.COLOR + "");
                event.setDeathMessage(message);
            }
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        unsetAfk(player);

        YamlConfiguration config = UltraVanilla.getConfig(player.getUniqueId());

        if (config.getBoolean("muted", false)) {
            player.sendMessage(Palette.WRONG + "You are muted.");
            event.setCancelled(true);
            return;
        }

        // Chat formatter
        String nameColor = ChatColor.valueOf(config.getString("name-color", "RESET")) + "";
        String textColor = ChatColor.valueOf(config.getString("text-color", "RESET")) + "";
        String group = plugin.getPermissions() != null ? plugin.getPermissions().getPrimaryGroup(player) : config.getString("default-group");
        String color = ChatColor.valueOf(plugin.getConfig().getString("color.rank." + group, "RESET")) + "";
        String abbreviation = group.substring(0, 1).toUpperCase();
        String rest = group.substring(1);
        String format;

        // Decide which format to show as specified in config.yml
        if (group.equals(plugin.getConfig().getString("default-group"))) {
            format = plugin.getConfig().getString("format.default");
        } else {
            format = plugin.getConfig().getString("format.prefixed");
        }

        // Replace all of the placeholders
        String formatted = Palette.translate(format)
                .replace("{name-color}", nameColor)
                .replace("{text-color}", textColor)
                .replace("{rank-color}", color)
                .replace("{colored-rank}", color + group + ChatColor.RESET)
                .replace("{colored-rank-abbreviation}", color + abbreviation + ChatColor.RESET)
                .replace("{colored-capitalized-rank}", color + abbreviation + rest + ChatColor.RESET)
                .replace("{rank}", group + ChatColor.RESET)
                .replace("{rank-abbreviation}", abbreviation + ChatColor.RESET)
                .replace("{capitalized-rank}", abbreviation + rest)
                .replace("{name}", "%1$s")
                .replace("{message}", "%2$s");
        event.setFormat(formatted);

        // Chat message
        String message = event.getMessage();

        // Chat color
        if (player.hasPermission("ultravanilla.chat.color")) {
            message = Palette.translate(message);
        }

        // Pings
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            String username = p.getName().toLowerCase();
            String name = ChatColor.stripColor(p.getDisplayName()).toLowerCase();
            for (String word : message.split(" ")) {
                if (word.length() >= 3 && word.startsWith("@")) {
                    word = word.substring(1);
                    word = word.replaceAll("[?.,]", "");
                    if (username.contains(word.toLowerCase()) || name.contains(word.toLowerCase())) {
                        if (UltraVanilla.getConfig(p.getUniqueId()).getBoolean(Users.PING_ENABLED, true) || UltraVanilla.isIgnored(player, p)) {
                            String at = Palette.NOUN + word + ChatColor.RESET;
                            plugin.ping(p);
                            message = message.replace("@" + word, at);
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
    }
}
