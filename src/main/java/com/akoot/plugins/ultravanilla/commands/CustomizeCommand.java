package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import net.md_5.bungee.api.ChatColor;
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

    public static final ChatColor COLOR = ChatColor.of("#f4c058");
    public static final ChatColor WRONG = ChatColor.of("#f47c58");
    public static final ChatColor RIGHT = ChatColor.of("#f4e258");

    public CustomizeCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    private void rename(Player player, ItemStack item, ItemMeta meta, String[] args, int index) {
        String newName = Palette.translate(String.join(" ", getArg(args, index)));
        meta.setDisplayName(ChatColor.RESET + newName);
        item.setItemMeta(meta);
        sendFormatted(player, "%sSet this %s%s %sname to: %s%s", COLOR, RIGHT, posessive(item.getType().name().toLowerCase()), COLOR, ChatColor.RESET, newName);
    }

    private void setLore(Player player, ItemStack item, ItemMeta meta, String[] args, int index) {
        String[] lore = Palette.translate(getArg(args, index)).split("\\|");
        for (int i = 0; i < lore.length; i++) {
            lore[i] = ChatColor.RESET + "" + ChatColor.WHITE + lore[i];
        }
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        sendFormatted(player, "%sSet this %s%s %slore to:\n%s", COLOR, RIGHT, posessive(item.getType().name().toLowerCase()), COLOR, String.join("\n", lore));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                sendFormatted(player, "%sItem invalid: %s%s", COLOR, WRONG, item.getType().name().toLowerCase());
                return true;
            }
            if (command.getName().equals("rename")) {
                rename(player, item, meta, args, 1);
            } else if (command.getName().equals("setlore")) {
                setLore(player, item, meta, args, 1);
            } else if (command.getName().equals("customize")) {
                if (args.length > 1) {
                    if (args[0].equalsIgnoreCase("name")) {
                        rename(player, item, meta, args, 2);
                    } else if (args[0].equalsIgnoreCase("lore")) {
                        setLore(player, item, meta, args, 2);
                    } else {
                        sendFormatted(player, "%sInvalid mode: %s%s", COLOR, WRONG, args[0]);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (command.getName().equals("customize")) {
            if (args.length == 1) {
                suggestions.add("name");
                suggestions.add("lore");
            }
        }
        return suggestions;
    }
}
