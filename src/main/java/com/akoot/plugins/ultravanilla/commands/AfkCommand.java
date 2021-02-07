package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Users;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AfkCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GRAY;

    public AfkCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Users.isAFK(player)) {
                    Users.afk.remove(player.getUniqueId());
                    plugin.getServer().broadcastMessage(player.getDisplayName() + color + " is no longer AFK");
                } else {
                    Users.afk.add(player.getUniqueId());
                    plugin.getServer().broadcastMessage(player.getDisplayName() + color + " is now AFK");
                }
                UltraVanilla.updateDisplayName(player);
            } else {
                sender.sendMessage(plugin.getString("player-only", "{action}", "toggle your AFK mode"));
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
