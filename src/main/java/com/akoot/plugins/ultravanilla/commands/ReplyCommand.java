package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
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

public class ReplyCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public ReplyCommand(Ultravanilla instance) {
        super(instance);
        this.color = ChatColor.GRAY;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            UUID senderId = Ultravanilla.CONSOLE_ID;
            String name = "Console";
            if (sender instanceof Player) {
                Player player = (Player) sender;
                senderId = player.getUniqueId();
                name = player.getDisplayName();
            }
            UUID recipientId = plugin.getMessages().get(senderId);
            if (recipientId != null) {
                Player recipient = plugin.getServer().getPlayer(recipientId);
                String message = String.join(" ", args).trim();
                if (recipient != null) {
                    sender.sendMessage(MessageCommand.formatPm("You", recipient.getDisplayName(), message));
                    recipient.sendMessage(MessageCommand.formatPm(name, "You", message));
                    if (plugin.getConfig().getBoolean("msg-ping")) {
                        recipient.playSound(recipient.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.5F);
                    }
                } else {
                    sender.sendMessage(MessageCommand.formatPm("You", "CONSOLE", message));
                    plugin.getLogger().info(ChatColor.stripColor(MessageCommand.formatPm(name, "You", message)));
                }
            } else {
                sender.sendMessage(wrong("You don't have anyone to reply to!"));
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
