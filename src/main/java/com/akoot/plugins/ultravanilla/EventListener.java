package com.akoot.plugins.ultravanilla;

import com.akoot.plugins.ultravanilla.util.Palette;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        String nick = plugin.getNicknames().getString(player.getUniqueId().toString());
        if (nick != null) {
            player.setDisplayName(nick + ChatColor.RESET);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        // Format
        String group = plugin.getPermissions().getPrimaryGroup(player);
        String color = ChatColor.valueOf(plugin.getColors().getString("ranks." + group, "RESET")) + "";
        String abbreviation = group.substring(0, 1).toUpperCase();
        String format = Palette.translate(group.equals(plugin.getConfig().getString("default-rank")) ? plugin.getConfig().getString("unranked-format") : plugin.getConfig().getString("ranked-format"));
        String formatted = format
                .replace("{rank-color}", color)
                .replace("{colored-rank}", color + group + ChatColor.RESET)
                .replace("{colored-rank-abbreviation}", color + abbreviation + ChatColor.RESET)
                .replace("{rank}", group)
                .replace("{rank-abbreviation}", abbreviation)
                .replace("{name}", "%1$s")
                .replace("{message}", "%2$s");
        event.setFormat(formatted);

        // Chat filter
        String message = event.getMessage();
        String newMessage = "";
        for (String word : message.split(" ")) {
            for (String swear : plugin.getSwears()) {
                Pattern p = Pattern.compile(swear);
                Matcher m = p.matcher(word.toLowerCase());
                if (m.find()) {
                    List<String> replacements = plugin.getBible().getStringList(plugin.getSwearsRaw().get(plugin.getSwears().indexOf(swear)));
                    int i = (int) Math.round(Math.random() * (replacements.size() - 1));
                    word = word.replace(m.group(0), replacements.get(i));
                }
            }
            newMessage += word + " ";
        }

        event.setMessage(newMessage);
    }
}
