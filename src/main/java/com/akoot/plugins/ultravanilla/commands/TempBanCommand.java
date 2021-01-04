package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.StaffAction;
import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.stuff.StringUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TempBanCommand extends AdminCommand implements CommandExecutor {

    public TempBanCommand(UltraVanilla instance) {
        super(instance);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 3) {
            OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);
            if (UltraVanilla.getConfig(target.getUniqueId()) != null) {
                if (!target.isBanned()) {
                    long time = (long) (StringUtil.getSeconds(args[1]) * 1000.0);
                    long now = System.currentTimeMillis();
                    long expires = now + time;
                    String reason = getArg(args, 3);
                    target.banPlayer(ChatColor.DARK_RED + Palette.translate(reason) + ChatColor.RESET + ChatColor.RED, new Date(expires), sender.getName());
                    StaffAction staffAction = new StaffAction(StaffAction.Type.TEMP_BAN, reason, sender.getName(), target.getName(), now, expires);
                    announce(staffAction);
                } else {
                    sender.sendMessage(PLAYER + target.getName() + COLOR + " is already banned!");
                }
            } else {
                sender.sendMessage(PLAYER + target.getName() + COLOR + " has not played before.");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        } else if (args.length == 2) {
            return new ArrayList<>();
        } else if (args.length == 3) {
            return getSuggestions(suggestions, args);
        }
        return new ArrayList<>();
    }
}
