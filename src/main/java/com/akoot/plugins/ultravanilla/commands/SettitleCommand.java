package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Users;
import com.akoot.plugins.ultravanilla.serializable.Title;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class SettitleCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public SettitleCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String newTitle = args[0].toLowerCase();
            boolean titleExists = false;
            for (Title title : Title.getTitles()) {
                if (title.getId().equals(newTitle)) {
                    titleExists = true;
                    break;
                }
            }
            if (!titleExists) {
                sender.sendMessage(error(command, "title-unknown", "{title}", newTitle));
                return true;
            }
            OfflinePlayer player = null;
            String message = "";
            if (args.length == 1) {
                if (sender instanceof Player) {
                    player = (Player) sender;
                    message = format(command, "title-set.self", "{title}", newTitle);
                } else {
                    sender.sendMessage(plugin.getString("player-only"));
                    return true;
                }
            } else if (args.length == 2) {
                player = plugin.getServer().getOfflinePlayer(args[0]);
                if (!player.hasPlayedBefore()) {
                    sender.sendMessage(plugin.getString("player-unknown"));
                    return true;
                }
                message = format(command, "title-set.other", "{player}", player.getName(), "{title}", newTitle);
            }
            if (player != null) {
                YamlConfiguration config = UltraVanilla.getConfig(player);
                List<String> unlockedTitles = config.getStringList(Users.UNLOCKED_TITLES);
                if (unlockedTitles.contains(newTitle) || sender.hasPermission("ultravanilla.admin")) {
                    config.set(Users.DISPLAY_TITLE, newTitle);
                    sender.sendMessage(message);
                }
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
