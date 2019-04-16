package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NickCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public NickCommand(Ultravanilla instance) {
        super(instance);
        color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (args[0].equals(player.getName())) {
                        player.setDisplayName(null);
                        player.setPlayerListName(player.getName());
                        sender.sendMessage(format("Your nickname was cleared!"));
                        Ultravanilla.set(player, Users.NICKNAME, null);
                    } else {
                        String newName = Palette.translate(args[0]);
                        sender.sendMessage(format("%s nickname is now %s", noun("Your"), reset(newName)));
                        player.setDisplayName(newName + ChatColor.RESET);
                        player.setPlayerListName(newName);
                        Ultravanilla.set(player, Users.NICKNAME, newName);
                    }
                } else {
                    sender.sendMessage(playerOnly());
                }
            } else if (args.length == 2) {
                if (sender.hasPermission("ultravanilla.command.nick.others")) {
                    Player target = plugin.getServer().getPlayer(args[0]);
                    String newName = Palette.translate(args[1]);
                    if (target != null) {
                        String username = target.getName();
                        sender.sendMessage(format("Set %s nickname to %s", posessiveNoun(username), reset(newName)));
                        target.setDisplayName(newName + ChatColor.RESET);
                        target.setPlayerListName(newName);
                        Ultravanilla.set(target, Users.NICKNAME, newName);
                    } else {
                        sender.sendMessage(playerNotFound(args[0]));
                    }
                } else {
                    sender.sendMessage(wrong("You do not have permission to change other people's nicknames!"));
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
