package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.serializable.Position;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class BackCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public BackCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player;
            if (args.length == 0) {
                player = (Player) sender;
            } else if (args.length == 1) {
                player = plugin.getServer().getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(plugin.getString("player-offline", "{player}", args[0]));
                    return true;
                }
            } else {
                return false;
            }
            Position position = (Position) UltraVanilla.getConfig(player).get("last-teleport");
            if (position != null) {
                player.teleport(position.getLocation());
            }
        } else {
            sender.sendMessage(plugin.getString("player-only", "{action}", "use /back"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}