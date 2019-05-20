package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Users;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PingCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GRAY;

    public PingCommand(UltraVanilla instance) {
        super(instance);
        color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    boolean pingEnabled = UltraVanilla.getConfig(player.getUniqueId()).getBoolean(Users.PING_ENABLED, true);
                    switch (args[0]) {
                        case "on":
                            if (pingEnabled) {
                                sender.sendMessage(format(command, "message.already-enabled"));
                            } else {
                                UltraVanilla.set(player.getUniqueId(), Users.PING_ENABLED, true);
                                sender.sendMessage(format(command, "message.enabled"));
                            }
                            break;
                        case "off":
                            if (!pingEnabled) {
                                sender.sendMessage(format(command, "message.already-enabled"));
                            } else {
                                UltraVanilla.set(player.getUniqueId(), Users.PING_ENABLED, false);
                                sender.sendMessage(format(command, "message.disabled"));
                            }
                            break;
                    }
                } else {
                    sender.sendMessage(plugin.getString("player-only", "{action}", "get pinged"));
                }
                return true;
            } else {
                for (Player player : getPlayers(args[0])) {
                    if (UltraVanilla.getConfig(player.getUniqueId()).getBoolean(Users.PING_ENABLED, true)) {
                        if (!(sender instanceof Player) || !UltraVanilla.isIgnored(player, (Player) sender)) {
                            player.sendMessage(format(command, "format.pinged", "{player}", sender.getName()));
                            sender.sendMessage(format(command, "format.ping", "{player}", player.getName()));
                            plugin.ping(player);
                        } else {
                            sender.sendMessage(plugin.getString("ignored", "{player}", player.getName()));
                        }
                        return true;
                    } else {
                        sender.sendMessage(format(command, "error.ping-disabled", "{player}", player.getName()));
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        } else {
            return new ArrayList<>();
        }
    }
}
