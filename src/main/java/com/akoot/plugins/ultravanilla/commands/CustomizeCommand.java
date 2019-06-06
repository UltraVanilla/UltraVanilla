package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomizeCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public CustomizeCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (args.length > 1) {
                    String mode = args[0];
                    if (mode.equalsIgnoreCase("name")) {
                        String name = Palette.translate(getArg(args, 2));
                        meta.setDisplayName(ChatColor.RESET + name);
                        sender.sendMessage(format(command, "message.set-name", "{item}", item.getType().toString(), "{name}", name));
                    } else if (mode.equalsIgnoreCase("lore")) {
                        String[] lore = Palette.translate(getArg(args, 2)).split("\\|");
                        for (int i = 0; i < lore.length; i++) {
                            lore[i] = ChatColor.RESET + lore[i];
                        }
                        meta.setLore(Arrays.asList(lore));
                        sender.sendMessage(format(command, "message.set-lore", "{item's}", posessive(item.getType().toString()), "{lore}", String.join("\n", lore)));
                    } else {
                        return false;
                    }
                    item.setItemMeta(meta);
                } else {
                    return false;
                }
            } else {
                sender.sendMessage(format(command, "error.invalid-item", "{item}", item.getType().toString()));
            }
        } else {
            sender.sendMessage(plugin.getString("player-only", "{action}", "customize items"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("name");
            suggestions.add("lore");
        }
        return suggestions;
    }
}
