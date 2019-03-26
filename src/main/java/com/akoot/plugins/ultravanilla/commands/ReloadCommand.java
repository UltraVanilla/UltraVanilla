package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public ReloadCommand(Ultravanilla instance) {
        super(instance);
        this.color = ChatColor.GOLD;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            plugin.loadConfigs();
            plugin.reloadConfig();
            sender.sendMessage(color + "Reloaded configs.");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
