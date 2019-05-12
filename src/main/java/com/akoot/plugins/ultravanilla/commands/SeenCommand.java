package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Users;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeenCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.YELLOW;

    public SeenCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
            if (player.hasPlayedBefore()) {
                if (args.length == 1) {
                    long lastJoin = UltraVanilla.getConfig(player.getUniqueId()).getLong(Users.LAST_LOGIN);
                    sender.sendMessage(format("%s was last seen on %s", noun(player.getName()), object(getDate(lastJoin))));
                } else if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("first")) {
                        long firstJoin = UltraVanilla.getConfig(player.getUniqueId()).getLong(Users.FIRST_LOGIN);
                        sender.sendMessage(format("%s first joined on %s", noun(player.getName()), object(getDate(firstJoin))));
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                sender.sendMessage(playerNotOnline(args[0]));
            }
        } else {
            return false;
        }
        return true;
    }

    private String getDate(long time) {
        Date date = new Date(time);
        DateFormat df = new SimpleDateFormat("E, M/dd/y G @ h:mma z");
        return df.format(date);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("first");
            return suggestions;
        }
        return null;
    }
}