package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class IgnoreCommand extends UltraCommand implements CommandExecutor {

    public static final ChatColor COLOR = ChatColor.RED;

    public IgnoreCommand(UltraVanilla instance) {
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
                List<String> ignored = UltraVanilla.getConfig(player.getUniqueId()).getStringList(Users.IGNORED);
                sender.sendMessage(plugin.getTitle(format(command, "format.list.title"), color));
                for (String id : ignored) {
                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(id));
                    sender.sendMessage(format(command, "format.list.item", "{player}", offlinePlayer.getName()));
                }
            } else if (args.length == 1) {
                Player p = plugin.getServer().getPlayer(args[0]);
                if (p != null) {
                    if (UltraVanilla.isIgnored(player, p)) {
                        sender.sendMessage(format(command, "message.unignore", "{name}", p.getName()));
                        UltraVanilla.remove(player.getUniqueId(), Users.IGNORED, p.getUniqueId().toString());
                    } else {
                        sender.sendMessage(format(command, "message.ignore", "{name}", p.getName()));
                        UltraVanilla.add(player.getUniqueId(), Users.IGNORED, p.getUniqueId().toString());
                    }
                } else {
                    sender.sendMessage(plugin.getString("player-offline", "{player}", args[0]));
                }
            } else {
                return false;
            }
        } else {
            sender.sendMessage(plugin.getString("player-only", "{action}", "ignore players"));
        }
        return true;
    }
}
