package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import com.akoot.plugins.ultravanilla.util.Palette;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NickCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public NickCommand(Ultravanilla instance) {
        super(instance);
        this.color = ChatColor.GRAY;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    String newName = Palette.translate(args[0]);
                    sender.sendMessage(format("%s nickname is now %s", noun("Your"), reset(newName)));
                    Player player = (Player) sender;
                    player.setDisplayName(newName + ChatColor.RESET);
                    plugin.saveNickname(player.getUniqueId(), newName);
                } else {
                    sender.sendMessage(playerOnly());
                }
            } else if (args.length == 2) {
                Player target = plugin.getServer().getPlayer(args[0]);
                String newName = Palette.translate(args[1]);
                if (target != null) {
                    String username = target.getName();
                    String possessive = username.endsWith("s") ? "'" : "'s";
                    sender.sendMessage(format("Set %s%s nickname to %s", noun(username), color + possessive, reset(newName)));
                    target.setDisplayName(newName + ChatColor.RESET);
                    plugin.saveNickname(target.getUniqueId(), newName);
                } else {
                    sender.sendMessage(playerNotFound(args[0]));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        }
        return new ArrayList<>();
    }
}
