package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
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

            title = Palette.translate(title.trim());
            subtitle = Palette.translate(subtitle.trim());

            String playersString = getArgFor(args, "-to");
            List<Player> players = new ArrayList<>(plugin.getServer().getOnlinePlayers());
            if (playersString != null) {
                players = getPlayers(playersString);
                if (title.contains("-to")) {
                    title = title.substring(0, title.indexOf("-to"));
                } else if (subtitle.contains("-to")) {
                    subtitle = subtitle.substring(0, subtitle.indexOf("-to"));
                }
            }
            for (Player player : players) {
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
            List<String> params = Arrays.asList(args);
            if (!params.contains("-s")) {
                list.add("-s");
            }
            if (!params.contains("-to")) {
                list.add("-to");
            }
        }

        return list;
    }
}
