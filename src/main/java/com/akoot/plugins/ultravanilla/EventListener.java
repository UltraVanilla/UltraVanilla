package com.akoot.plugins.ultravanilla;

import com.akoot.plugins.ultravanilla.commands.AfkCommand;
import com.akoot.plugins.ultravanilla.commands.MuteCommand;
import com.akoot.plugins.ultravanilla.commands.SuicideCommand;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import com.akoot.plugins.ultravanilla.serializable.Position;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;

import java.io.IOException;
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

        // suggest their role automatically
        String[] roles = {"default", "member", "loyalist", "pro", "master", "elder", "grandmaster", "sage-1", "sage-2"};
        for (int i = 0; i < roles.length - 1; i++) {
            String role = roles[i];
            String nextRole = roles[i + 1];
            if (plugin.getPermissions().getPrimaryGroup(player).equals(role)) {
                if (UltraVanilla.getPlayTime(player) / 60000L >= plugin.getConfig().getInt("times." + nextRole)) {
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        if (p.hasPermission("ultravanilla.moderator")) {
                            p.sendMessage(String.format(Palette.translate("&d%s&6 should be a &7%s&6 by now!"), player.getName(), nextRole));
                        }
                    }
                }
            }
        }
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
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        unsetAfk(player);

        YamlConfiguration config = UltraVanilla.getConfig(player.getUniqueId());

        if (config.getBoolean("muted", false)) {
            player.sendMessage(MuteCommand.COLOR + "You are muted.");
            event.setCancelled(true);
            plugin.getLogger().info(player.getName() + " tried to say: " + event.getMessage());
            return;
        }

        // Chat message
        String message = event.getMessage();

        // Chat color
        if (player.hasPermission("ultravanilla.chat.color")) {
            message = Palette.translate(message);
        }

        // Pings
        if (message.contains("@")) {
            Pattern p = Pattern.compile("@([a-zA-Z0-9]{2,})");
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
                        if (recipient.getName().toLowerCase().contains(name) || ChatColor.stripColor(recipient.getDisplayName()).toLowerCase().contains(name)) {
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
        String textPrefix = config.getString("text-prefix", ChatColor.RESET + "");
        String group = plugin.getPermissions().getPrimaryGroup(player);
        String rankColor = ChatColor.of(plugin.getConfig().getString("color.rank." + group, "RESET")) + "";
        String rank = plugin.getConfig().getString("rename-groups." + group, group.substring(0, 1).toUpperCase() + group.substring(1));

        String donatorBracketsColor = ChatColor.of(plugin.getConfig().getString("color.chat.brackets.donator")) + "";
        String rankBracketsColor = ChatColor.of(plugin.getConfig().getString("color.chat.brackets.rank")) + "";
        String nameBracketsColor = ChatColor.of(plugin.getConfig().getString("color.chat.brackets.name")) + "";
        String defaultNameColor = ChatColor.of(plugin.getConfig().getString("color.chat.default-name-color")) + "";

        String format = String.format("%s%s[%s%s§r%s] %s<%s§r%s> %s%s",
                (donator ? String.format("%s[%sD%s] ",
                        donatorBracketsColor, ChatColor.of(plugin.getConfig().getString("color.rank.donator")), donatorBracketsColor)
                        : ""),
                rankBracketsColor, rankColor, rank, rankBracketsColor,
                nameBracketsColor, defaultNameColor + "%1$s", nameBracketsColor,
                textPrefix, "%2$s");


        // Replace all of the placeholders
        String formatted = Palette.translate(format);
        event.setFormat(formatted);
    }
}
