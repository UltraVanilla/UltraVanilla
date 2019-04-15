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

import java.util.List;

public class PingCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.LIGHT_PURPLE;

    public PingCommand(Ultravanilla instance) {
        super(instance);
        color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    boolean pingEnabled = Ultravanilla.getConfig(player.getUniqueId()).getBoolean(Users.PING_ENABLED, true);
                    switch (args[0]) {
                        case "on":
                            if (pingEnabled) {
                                sender.sendMessage(format("Ping already enabled!"));
                            } else {
                                Ultravanilla.set(player.getUniqueId(), Users.PING_ENABLED, true);
                                sender.sendMessage(format("%s ping", Palette.TRUE + "Enabled" + color));
                            }
                            break;
                        case "off":
                            if (!pingEnabled) {
                                sender.sendMessage(format("Ping already disabled!"));
                            } else {
                                Ultravanilla.set(player.getUniqueId(), Users.PING_ENABLED, false);
                                sender.sendMessage(format("%s ping", Palette.FALSE + "Disabled" + color));
                            }
                            break;
                    }
                } else {
                    sender.sendMessage(playerOnly());
                }
            } else {
                Player player = plugin.getServer().getPlayer(args[0]);
                if (player != null) {
                    player.sendMessage(format("%s pinged you!", noun(sender.getName())));
                    sender.sendMessage(format("You pinged %s!", noun(player.getName())));
                    plugin.ping(player);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
