package com.akoot.plugins.ultravanilla;

import com.akoot.plugins.ultravanilla.commands.AfkCommand;
import com.akoot.plugins.ultravanilla.commands.PingCommand;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventListener implements Listener {

    private Ultravanilla plugin;

    public EventListener(Ultravanilla instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        AfkCommand.setAFK(player, false);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        AfkCommand.setAFK(player, false);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        OfflinePlayer[] offlinePlayers = plugin.getServer().getOfflinePlayers();
        OfflinePlayer offlinePlayer = offlinePlayers[plugin.getRandom().nextInt(offlinePlayers.length)];
        assert offlinePlayer.getName() != null;
        String name = offlinePlayer.getName();
        String version = plugin.getServer().getVersion();
        version = version.substring(version.indexOf("MC: ") + 4, version.length() - 1);
        event.setMotd(Palette.translate(plugin.getConfig().getString("motd.server-name")) +
                " " + ChatColor.valueOf(plugin.getConfig().getString("motd.version-color")) + version +
                "\n" + ChatColor.RESET + plugin.getMOTD().replace("%player", name));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String nick = Ultravanilla.getConfig(player.getUniqueId()).getString(Users.NICKNAME);
        String nameColor = ChatColor.valueOf(Ultravanilla.getConfig(player.getUniqueId()).getString("name-color", "RESET")) + "";
        if (nick != null) {
            player.setDisplayName(nick + ChatColor.RESET);
            player.setPlayerListName(nick);
        }
        player.setPlayerListName(nameColor + player.getPlayerListName());
        Ultravanilla.set(player, Users.LAST_LOGIN, System.currentTimeMillis());
        if (!player.hasPlayedBefore()) {
            Ultravanilla.set(player, Users.FIRST_LOGIN, System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.hasPermission("ultravanilla.command.suicide")) {
            String message = event.getDeathMessage();
            if (message != null && message.endsWith(" died")) {
                List<String> messages = plugin.getConfig().getStringList("strings.suicide-message");
                message = messages.get(plugin.getRandom().nextInt(messages.size()));
                event.setDeathMessage(String.format(message, player.getName()));
            }
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        AfkCommand.setAFK(player, false);

        YamlConfiguration config = Ultravanilla.getConfig(player.getUniqueId());

        if (config.getBoolean("muted", false)) {
            player.sendMessage(Palette.WRONG + "You are muted.");
            event.setCancelled(true);
            return;
        }

        // Chat formatter
        String nameColor = ChatColor.valueOf(config.getString("name-color", "RESET")) + "";
        String textColor = ChatColor.valueOf(config.getString("text-color", "RESET")) + "";
        String group = plugin.getPermissions().getPrimaryGroup(player);
        String color = ChatColor.valueOf(plugin.getConfig().getString("color.rank." + group, "RESET")) + "";
        String abbreviation = group.substring(0, 1).toUpperCase();
        String rest = group.substring(1);
        String format;

        // Decide which format to show as specified in config.yml
        if (group.equals(plugin.getConfig().getString("default-rank"))) {
            format = plugin.getConfig().getString("default-format");
        } else {
            format = plugin.getConfig().getString("ranked-format");
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

        // Chat filter
        String message = event.getMessage();
        if (plugin.getConfig().getBoolean("enable-chat-filter") && !player.hasPermission("ultravanilla.chat.swearing")) {
            String newMessage = "";
            for (String word : message.split(" ")) {
                for (String swear : plugin.getSwearsRegex()) {
                    Pattern p = Pattern.compile(swear);
                    Matcher m = p.matcher(word.toLowerCase());
                    if (m.find()) {
                        List<String> replacements = plugin.getSwears().getStringList(plugin.getSwearsRaw().get(plugin.getSwearsRegex().indexOf(swear)));
                        word = word.toLowerCase().replace(m.group(0), replacements.get(plugin.getRandom().nextInt(replacements.size())));
                    }
                }
                newMessage += word + " ";
            }
            message = newMessage;
        }

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
                    if (username.contains(word.toLowerCase()) || name.contains(word.toLowerCase())) {
                        if (Ultravanilla.getConfig(p.getUniqueId()).getBoolean(Users.PING_ENABLED, true) || Ultravanilla.isIgnored(player, p)) {
                            String at = PingCommand.COLOR + word + ChatColor.RESET;
                            plugin.ping(p);
                            message = message.replace("@" + word, at);
                        }
                    }
                }
            }
        }

        //ignored
        for (Player p : event.getRecipients()) {
            if (Ultravanilla.isIgnored(p, player)) {
                event.getRecipients().remove(p);
            }
        }

        event.setMessage(message);
    }
}
