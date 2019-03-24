package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TitleCommand extends UltraCommand implements CommandExecutor, TabCompleter {

    public TitleCommand(Ultravanilla plugin) {
        super(plugin);
        this.color = ChatColor.DARK_RED;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length >= 1) {
            List<String> args2 = Arrays.asList(args);

            String title = "";
            String subtitle = "";

            int subFlag = args.length;
            if (args2.contains("-s")) {
                subFlag = args2.indexOf("-s");
                for (int i = subFlag + 1; i < args.length; i++) {
                    subtitle = subtitle + args[i] + " ";
                }
            }

            for (int i = 0; i < subFlag; i++) {
                title = title + args[i] + " ";

            }

            title = ChatColor.translateAlternateColorCodes('&', title.trim());
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle.trim());
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendTitle(title, subtitle);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        // Initialize the list as empty
        List<String> list = new ArrayList<>();
        if (args.length > 1) {
            if (!Arrays.asList(args).contains("-s")) {
                list.add("-s");
            }
        }

        return list;
    }
}
