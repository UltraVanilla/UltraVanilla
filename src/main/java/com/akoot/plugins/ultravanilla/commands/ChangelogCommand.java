package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class ChangelogCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GRAY;

    public ChangelogCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            YamlConfiguration changelog = plugin.getChangelog();
            String title = format(command, "format.list.title.player", "{name}", plugin.getDescription().getName(), "{version}", plugin.getDescription().getVersion());
            sender.sendMessage(plugin.getTitle(title, color));
            for (String fixed : changelog.getStringList("player.fixed")) {
                sender.sendMessage(format(command, "format.list.item.fixed", "{message}", fixed));
            }
            for (String added : changelog.getStringList("player.added")) {
                sender.sendMessage(format(command, "format.list.item.added", "{message}", added));
            }
            for (String removed : changelog.getStringList("player.removed")) {
                sender.sendMessage(format(command, "format.list.item.removed", "{message}", removed));
            }
            for (String removed : changelog.getStringList("player.changed")) {
                sender.sendMessage(format(command, "format.list.item.changed", "{message}", removed));
            }
            for (String removed : changelog.getStringList("player.broke")) {
                sender.sendMessage(format(command, "format.list.item.broke", "{message}", removed));
            }
            if (sender.hasPermission("ultravanilla.command.changelog.staff")) {
                title = format(command, "format.list.title.staff");
                sender.sendMessage(plugin.getTitle(title, color));
                for (String fixed : changelog.getStringList("staff.fixed")) {
                    sender.sendMessage(format(command, "format.list.item.fixed", "{message}", fixed));
                }
                for (String added : changelog.getStringList("staff.added")) {
                    sender.sendMessage(format(command, "format.list.item.added", "{message}", added));
                }
                for (String removed : changelog.getStringList("staff.removed")) {
                    sender.sendMessage(format(command, "format.list.item.removed", "{message}", removed));
                }
                for (String removed : changelog.getStringList("staff.changed")) {
                    sender.sendMessage(format(command, "format.list.item.changed", "{message}", removed));
                }
                for (String removed : changelog.getStringList("staff.broke")) {
                    sender.sendMessage(format(command, "format.list.item.broke", "{message}", removed));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
