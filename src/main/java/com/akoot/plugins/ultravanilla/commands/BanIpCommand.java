package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.StaffAction;
import com.akoot.plugins.ultravanilla.UltraVanilla;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class BanIpCommand extends AdminCommand implements CommandExecutor, TabExecutor {

    public BanIpCommand(UltraVanilla instance) {
        super(instance);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            Player target = plugin.getServer().getPlayer(args[0]);
            String reason = getArg(args, 2);
            String source = sender.getName();
            String ip;
            StaffAction staffAction;
            if (target != null) {
                target.banPlayerIP(ChatColor.DARK_RED + reason + ChatColor.RESET, source);
                staffAction = new StaffAction(StaffAction.Type.IP_BAN, reason, source, target.getName());
            } else {
                ip = args[0];
                if (!plugin.getServer().getIPBans().contains(ip)) {
                    plugin.getServer().banIP(ip);
                    staffAction = new StaffAction(StaffAction.Type.IP_BAN, reason, source, ip);
                } else {
                    sender.sendMessage(PLAYER + ip + COLOR + " is already banned!");
                    return true;
                }
            }
            announce(staffAction);
            return true;
        }
        return false;
    }
}
