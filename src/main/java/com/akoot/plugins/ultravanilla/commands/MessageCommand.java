package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import com.akoot.plugins.ultravanilla.util.Palette;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    private static String format;

    public MessageCommand(Ultravanilla instance) {
        super(instance);
        this.color = ChatColor.GRAY;
        format = instance.getConfig().getString("msg-format");
    }

    public static String formatPm(String from, String to, String message) {
        return Palette.translate(format
                .replace("{from}", from)
                .replace("{to}", to)
                .replace("{message}", message));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            String message = String.join(" ", args);
            message = message.substring(message.indexOf(" ")).trim();
            String name = sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName();
            if (args[0].equalsIgnoreCase("console")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    sender.sendMessage(MessageCommand.formatPm("CONSOLE", "You", message));
                    plugin.getLogger().info(ChatColor.stripColor(MessageCommand.formatPm("You", name, message)));
                    plugin.getMessages().put(player.getUniqueId(), Ultravanilla.CONSOLE_ID);
                } else {
                    sender.sendMessage(playerOnly());
                }
            } else {
                Player recipient = plugin.getServer().getPlayer(args[0]);
                if (recipient != null) {
                    sender.sendMessage(MessageCommand.formatPm(recipient.getDisplayName(), "You", message));
                    recipient.sendMessage(MessageCommand.formatPm("You", name, message));
                    UUID id = sender instanceof Player ? ((Player) sender).getUniqueId() : Ultravanilla.CONSOLE_ID;
                    plugin.getMessages().put(id, recipient.getUniqueId());
                    if (plugin.getConfig().getBoolean("msg-ping")) {
                        recipient.playSound(recipient.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.5F);
                    }
                } else {
                    sender.sendMessage(playerNotFound(args[0]));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        }
        return new ArrayList<>();
    }
}
