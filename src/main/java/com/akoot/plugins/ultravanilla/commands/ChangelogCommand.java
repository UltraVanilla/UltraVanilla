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
            sender.sendMessage(title(String.format("%s %s %s", object(plugin.getDescription().getName()), number(plugin.getDescription().getVersion()), object("Changelog"))));
            for (String fixed : changelog.getStringList("player.fixed")) {
                sender.sendMessage(ChatColor.YELLOW + "* Fixed " + color + fixed);
            }
            for (String added : changelog.getStringList("player.added")) {
                sender.sendMessage(ChatColor.GREEN + "+ Added " + color + added);
            }
            for (String removed : changelog.getStringList("player.removed")) {
                sender.sendMessage(ChatColor.RED + "- Removed " + color + removed);
            }
            if (sender.hasPermission("ultravanilla.command.changelog.staff")) {
                sender.sendMessage(title(format("%s", noun("Staff Section"))));
                for (String fixed : changelog.getStringList("staff.fixed")) {
                    sender.sendMessage(ChatColor.YELLOW + "* Fixed " + color + fixed);
                }
                for (String added : changelog.getStringList("staff.added")) {
                    sender.sendMessage(ChatColor.GREEN + "+ Added " + color + added);
                }
                for (String removed : changelog.getStringList("staff.removed")) {
                    sender.sendMessage(ChatColor.RED + "- Removed " + color + removed);
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
