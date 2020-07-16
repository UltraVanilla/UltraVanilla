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

public class CreatetitleCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public CreatetitleCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        List<Title> titles = Title.getTitles();

        // creattitle <name> <rarity> <displayname>
        if (args.length >= 3) {
            String id = args[0].toLowerCase();
            Title.Rarity rarity;
            try {
                rarity = Title.Rarity.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(error(command, "rarity-unknown", "{rarity}", args[1]));
                return true;
            }
            String name = getArg(args, 2);
            for (Title title : Title.getTitles()) {
                if (title.getId().equals(id)) {
                    sender.sendMessage(error(command, "title-already-exists", "{title}", id));
                    return true;
                }
            }
            Title.addTitle(new Title(id, name, rarity));

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
