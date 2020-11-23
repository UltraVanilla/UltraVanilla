package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class DoCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public DoCommand(UltraVanilla instance) {
        super(instance);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        args = refinedArgs(args);
        long timer = 0;
        if (args[0].startsWith("delay:")) {
            timer = (long) (Double.parseDouble(args[0].substring(args[0].indexOf(":") + 1)) * 20.0);
        }
        if (timer > 0) {
            for (int i = 1, argsLength = args.length; i < argsLength; i++) {
                String arg = args[i];
                Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.dispatchCommand(sender, arg), timer * (i - 1));
            }
        } else {
            for (String arg : args) {
                Bukkit.dispatchCommand(sender, arg);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
