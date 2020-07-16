package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.serializable.Title;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class RemoveTitleCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public RemoveTitleCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        List<Title> titles = Title.getTitles();

        // removetitle <name>
        if (args.length == 1) {
            String id = args[0].toLowerCase();

            for (Title title : Title.getTitles()) {
                if (title.getId().equals(id)) {
                    Title.removeTitle(id);
                    sender.sendMessage(format(command, "removed", "{title}", id));
                    return true;
                }
            }
            sender.sendMessage(error(command, "title-unknown", "{title}", id));

            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        return null;
    }
}