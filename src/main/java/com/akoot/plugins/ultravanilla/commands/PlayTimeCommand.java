package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayTimeCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.AQUA;

    public PlayTimeCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    private static String getPlayTime(OfflinePlayer player) {
        long playtime = UltraVanilla.getPlayTime(player);
        long seconds = (playtime / 1000) % 60;
        long minutes = (playtime / (1000 * 60)) % 60;
        long hours = (playtime / (1000 * 60 * 60)) % 24;
        return String.format(
                "§d%s §bhas played for §6%s%s%s",
                player.getName(),
                hours != 0 ? hours + "h" : "",
                minutes != 0 ? minutes + "m" : "",
                seconds != 0 ? seconds + "s" : ""
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getString("player-only", "{action}", "check your playtime"));
                return true;
            }
            sender.sendMessage(getPlayTime((OfflinePlayer) sender));
        } else if (args.length == 1) {
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
            if (!player.hasPlayedBefore()) {
                sender.sendMessage(plugin.getString("player-unknown", "{player}", args[0]));
                return true;
            }
            sender.sendMessage(getPlayTime(player));
        } else {
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
