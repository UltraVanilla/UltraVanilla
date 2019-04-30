package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AfkCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GRAY;

    public AfkCommand(Ultravanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    public static void setAFK(Player player, boolean mode) {
        if (mode != Users.AFK.contains(player.getUniqueId())) {
            Bukkit.getServer().broadcastMessage(String.format("%s is %s AFK", Palette.NOUN + (player.getName()) + COLOR, mode ? "now" : "no longer"));
            if (mode) {
                Users.AFK.add(player.getUniqueId());
            } else {
                Users.AFK.remove(player.getUniqueId());
            }
        }
    }

    public static void toggleAFK(Player player) {
        setAFK(player, !Users.AFK.contains(player.getUniqueId()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                toggleAFK(player);
            } else {
                sender.sendMessage(playerOnly());
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
