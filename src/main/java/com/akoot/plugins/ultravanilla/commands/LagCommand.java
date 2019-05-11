package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import net.minecraft.server.v1_14_R1.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LagCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GRAY;

    public LagCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int i = 0;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("average")) {
                sender.sendMessage(title("Average lag"));
                i = 2;
            } else if (args[0].equalsIgnoreCase("median")) {
                sender.sendMessage(title("Median lag"));
                i = 1;
            }
        } else {
            sender.sendMessage(title("Current lag"));
        }
        double tps = MinecraftServer.getServer().recentTps[i];
        double percent = (1.0 - (tps / 20.0)) * 100.0;
        if (percent < 0) {
            percent = 0.0;
        }
        String percentString = String.format("%.1f", percent) + "%";
        String tpsString = String.format("%.1f", tps);
        if (sender instanceof Player) {
            int ping = ((CraftPlayer) sender).getHandle().ping;
            sender.sendMessage(format("Ping: %s", number(ping + "ms")));
        }
        sender.sendMessage(format("TPS: %s", number(tpsString)));
        sender.sendMessage(format("The server is %s laggy", number(percentString)));
        ;
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("average");
            suggestions.add("median");
        }
        return suggestions;
    }
}
