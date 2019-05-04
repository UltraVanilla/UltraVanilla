package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InventoryCommand extends UltraCommand implements CommandExecutor {

    public static final ChatColor COLOR = ChatColor.DARK_AQUA;

    public InventoryCommand(Ultravanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 || args.length == 2) {
            Player player = plugin.getServer().getPlayer(args[0]);
            if (args[0].equalsIgnoreCase("self")) {
                if (sender instanceof Player) {
                    player = (Player) sender;
                } else {
                    sender.sendMessage(playerOnly());
                }
            }
            if (player != null) {
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        ((Player) sender).openInventory(player.getInventory());
                    } else {
                        sender.sendMessage(format("%s's inventory:\n%s", player.getName(), player.getInventory().toString()));
                    }
                } else {
                    if (args[1].equalsIgnoreCase("enderchest")) {
                        if (sender instanceof Player) {
                            ((Player) sender).openInventory(player.getEnderChest());
                        } else {
                            sender.sendMessage(format("%s's ender chest:\n%s", player.getName(), player.getEnderChest().toString()));
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                sender.sendMessage(playerNotFound(args[0]));
            }
            return true;
        }
        return false;
    }
}
