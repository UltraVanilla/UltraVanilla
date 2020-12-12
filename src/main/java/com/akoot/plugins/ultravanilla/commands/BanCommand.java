package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.StaffAction;
import com.akoot.plugins.ultravanilla.UltraVanilla;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public class BanCommand extends AdminCommand implements CommandExecutor, TabExecutor {

    public BanCommand(UltraVanilla instance) {
        super(instance);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);
            if (UltraVanilla.getConfig(target.getUniqueId()) != null) {
                if (!target.isBanned()) {
                    String reason = getArg(args, 2);
                    String source = sender.getName();
                    target.banPlayer(ChatColor.DARK_RED + reason + ChatColor.RESET, source);
                    StaffAction staffAction = new StaffAction(StaffAction.Type.BAN, reason, source, target.getName());
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
}
