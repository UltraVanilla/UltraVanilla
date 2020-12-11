package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.stuff.StringUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TempBanCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.of("#ed521e");
    public static final ChatColor PLAYER = ChatColor.of("#f7a204");
    public static final ChatColor TIME = ChatColor.of("#f7d31d");

    public TempBanCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
            if (UltraVanilla.getConfig(player.getUniqueId()) != null) {
                if (!player.isBanned()) {
                    long time = 0;
                    if (args.length == 1) {
                        time = StringUtil.getSeconds("24h") * 1000L;
                    } else if (args.length == 2) {
                        time = StringUtil.getSeconds(args[1]) * 1000L;
                    }
                    String timeString = StringUtil.getTimeString(time);
                    sender.sendMessage(COLOR + "Temporarily banned " + PLAYER + player.getName() + COLOR + " for " + TIME + timeString);
                    player.banPlayer(Palette.translate(plugin.getString("tempban-message", "%time%", timeString)), new Date(System.currentTimeMillis() + time));
                } else {
                    sender.sendMessage(PLAYER + player.getName() + COLOR + " is already banned!");
                }
            } else {
                sender.sendMessage(PLAYER + player.getName() + COLOR + " has not played before.");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            return suggestions;
        }
        return null;
    }
}
