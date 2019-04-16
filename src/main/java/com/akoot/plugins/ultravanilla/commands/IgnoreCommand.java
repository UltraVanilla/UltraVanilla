package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class IgnoreCommand extends UltraCommand implements CommandExecutor {

    public static final ChatColor COLOR = ChatColor.RED;

    public IgnoreCommand(Ultravanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    public static String ignoredMessage(Player player) {
        return String.format("%s %sis ignoring you", Palette.NOUN + player.getName(), COLOR + "");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                List<String> ignored = Ultravanilla.getConfig(player.getUniqueId()).getStringList(Users.IGNORED);
                sender.sendMessage(format("Ignored players"));
                for (String id : ignored) {
                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(id));
                    sender.sendMessage(offlinePlayer.getName());
                }
            } else if (args.length == 1) {
                Player p = plugin.getServer().getPlayer(args[0]);
                if (p != null) {
                    if (Ultravanilla.isIgnored(player, p)) {
                        sender.sendMessage(format("Stopped ignoring %s", noun(p.getName())));
                        Ultravanilla.remove(player.getUniqueId(), Users.IGNORED, p.getUniqueId().toString());
                    } else {
                        if (!p.hasPermission("ultravanilla.command.ignore.bypass")) {
                            sender.sendMessage(format("Ignored %s", noun(p.getName())));
                            Ultravanilla.add(player.getUniqueId(), Users.IGNORED, p.getUniqueId().toString());
                        } else {
                            sender.sendMessage(format("You can't ignore that player"));
                        }
                    }
                } else {
                    sender.sendMessage(playerNotFound(args[0]));
                }
            }
        } else {
            sender.sendMessage(playerOnly());
        }
        return false;
    }
}
