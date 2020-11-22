
package com.akoot.plugins.ultravanilla.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.akoot.plugins.ultravanilla.UltraVanilla;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;

public class PromoteCommand extends UltraCommand implements TabExecutor {
    public static final ChatColor COLOR = ChatColor.GREEN;
    private Permission permissions;

    public PromoteCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
        permissions = instance.getPermissions();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (String playerName : args) {
            String demoteFrom = "default";
            String promoteTo = null;

            Player player = Bukkit.getPlayer(playerName);

            if (player != null && !plugin.hasRightRole(player)) {
                ArrayList<String> roleOrder = new ArrayList<>(Arrays.asList(plugin.getAllTimedRoles()));
                roleOrder.add(0, "default");

                String existingRole = permissions.getPrimaryGroup(player);
                int currentPos = roleOrder.indexOf(existingRole);

                // prevents demoting staff and/or special roles
                if (currentPos == -1) continue;

                promoteTo = roleOrder.get(Math.min(currentPos + 1, roleOrder.size() - 1));

                demoteFrom = existingRole;
            }

            if (promoteTo != null) {
                sender.sendMessage(format(command, "format.promoted", "{player}", player.getName(), "{rank}", promoteTo));

                permissions.playerRemoveGroup(player, demoteFrom);
                permissions.playerAddGroup(player, promoteTo);
            } else {
                sender.sendMessage(format(command, "format.did-not-promote", "{player}", playerName));
            }
        }

        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
