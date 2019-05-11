package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class MotdCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GREEN;

    public MotdCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        args = refinedArgs(args);
        if (args.length == 0) {
            sender.sendMessage(plugin.getMOTD().replace("%player", sender.getName()));
        } else if (args.length == 1) {
            if (sender.hasPermission("ultravanilla.command.motd.set")) {
                plugin.setMOTD(args[0]);
                sender.sendMessage(format("MOTD was set to: %s", ChatColor.RESET + Palette.translate(args[0])));
            } else {
                sender.sendMessage(noPermission("set the MOTD"));
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (sender.hasPermission("ultravanilla.command.motd.set")) {
            for (String motd : plugin.getMotds()) {
                suggestions.add("\"" + motd + "\"");
            }
        }
        return suggestions;
    }
}
