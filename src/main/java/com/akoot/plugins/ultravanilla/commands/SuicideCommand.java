package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SuicideCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.RED;

    public SuicideCommand(UltraVanilla instance) {
        super(instance);
        color = COLOR;
    }

    private boolean isInPact(Player suicider, Player player) {
        String pactId = UltraVanilla.getConfig(suicider.getUniqueId()).getString("suicide-pact");
        if (pactId != null) {
            return player.getUniqueId().equals(UUID.fromString(pactId));
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player suicider = (Player) sender;
            if (args.length == 0) {
                suicider.setHealth(0);
                sender.sendMessage(format(command, "message.kill-self"));
                String pactId = UltraVanilla.getConfig(suicider.getUniqueId()).getString("suicide-pact");
                if (pactId != null && !pactId.isEmpty()) {
                    Player player = plugin.getServer().getPlayer(UUID.fromString(pactId));
                    if (player != null) {
                        player.setHealth(0);
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("pact")) {
                    if (suicider.hasPermission("ultravanilla.command.suicide.pact")) {
                        Player player = plugin.getServer().getPlayer(args[1]);
                        if (player != null) {
                            if (!isInPact(suicider, player)) {
                                sender.sendMessage(message(command, "pact.create", "{player}", player.getName()));
                                UltraVanilla.set(suicider, "suicide-pact", player.getUniqueId().toString());
                            } else {
                                sender.sendMessage(message(command, "pact.cease", "{player}", player.getName()));
                                UltraVanilla.set(suicider, "suicide-pact", null);

                            }
                        } else {
                            sender.sendMessage(plugin.getString("player-offline", "{player}", args[1]));
                        }
                    } else {
                        sender.sendMessage(plugin.getString("no-permission", "{action}", "make a suicide pact with anyone"));
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            sender.sendMessage(plugin.getString("player-only", "{action}", "kill yourself"));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
