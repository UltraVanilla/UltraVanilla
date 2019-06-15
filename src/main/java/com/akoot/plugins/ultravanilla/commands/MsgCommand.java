package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MsgCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public MsgCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    public static void msg(CommandSender from, CommandSender to, String message) {
        UltraVanilla plugin = UltraVanilla.getInstance();
        String fromFormat = plugin.getString("command.message.format.from");
        String toFormat = plugin.getString("command.message.format.to");
        String spyFormat = plugin.getString("command.message.format.spy");

        from.sendMessage(Palette.translate(toFormat
                .replace("{message}", message)
                .replace("{recipient}", to.getName())
        ));

        to.sendMessage(Palette.translate(fromFormat
                .replace("{message}", message)
                .replace("{player}", from.getName())
        ));

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("ultravanilla.chat.spy")
                    && !(player.getName().equals(from.getName())
                    || player.getName().equals(to.getName()))) {
                player.sendMessage(Palette.translate(spyFormat
                        .replace("{message}", message)
                        .replace("{player}", from.getName())
                        .replace("{recipient}", to.getName())
                ));
            }
        }

        Users.REPLIES.put(from.getName(), to.getName());
        Users.REPLIES.put(to.getName(), from.getName());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            CommandSender to;
            if (args[0].equalsIgnoreCase("console")) {
                to = Bukkit.getConsoleSender();
            } else {
                to = plugin.getServer().getPlayer(args[0]);
            }
            if (to != null) {
                msg(sender, to, getArg(args, 2));
            } else {
                sender.sendMessage(format(command, "player-offline", "{player}", args[0]));
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        } else {
            return new ArrayList<>();
        }
    }
}
