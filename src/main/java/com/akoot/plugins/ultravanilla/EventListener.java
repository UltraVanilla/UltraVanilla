package com.akoot.plugins.ultravanilla;

import com.akoot.plugins.ultravanilla.commands.PingCommand;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventListener implements Listener {

    private Ultravanilla plugin;

    public EventListener(Ultravanilla instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String nick = Ultravanilla.getConfig(player.getUniqueId()).getString(Users.NICKNAME);
        if (nick != null) {
            player.setDisplayName(nick + ChatColor.RESET);
            player.setPlayerListName(nick);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.hasPermission("ultravanilla.command.suicide")) {
            String message = event.getDeathMessage();
            if (message != null && message.endsWith(" died")) {
                List<String> messages = plugin.getConfig().getStringList("suicide-message");
                message = messages.get(plugin.getRandom().nextInt(messages.size()));
                event.setDeathMessage(String.format(message, player.getName()));
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

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
            String username = p.getName();
            String name = ChatColor.stripColor(p.getDisplayName());
            for (String word : message.split(" ")) {
                if (username.toLowerCase().contains(word.toLowerCase()) || name.toLowerCase().contains(word.toLowerCase())) {
                    if (Ultravanilla.getConfig(p.getUniqueId()).getBoolean(Users.PING_ENABLED, true)) {
                        String at = PingCommand.COLOR + word + ChatColor.RESET;
                        plugin.ping(p);
                        message = message.replace(word, at);
                    }
                }
            }
        }

        event.setMessage(message);
    }
}
