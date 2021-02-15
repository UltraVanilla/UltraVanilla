package world.ultravanilla.commands;

import world.ultravanilla.UltraVanilla;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import world.ultravanilla.UltraVanilla;

import java.util.ArrayList;
import java.util.List;

public class InventoryCommand extends UltraCommand implements CommandExecutor {

    public static final ChatColor COLOR = ChatColor.DARK_AQUA;

    public InventoryCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 || args.length == 2) {
            Player player = plugin.getServer().getPlayer(args[0]);
            if (args[0].equalsIgnoreCase("self")) {
                if (sender instanceof Player) {
                    player = (Player) sender;
                } else {
                    sender.sendMessage(plugin.getString("player-only", "{action}", "open your inventory"));
                }
            }
            if (player != null) {
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        ((Player) sender).openInventory(player.getInventory());
                    } else {
                        sender.sendMessage(String.format("%s's inventory:\n%s", player.getName(), getInventoryList(player.getInventory().getContents())));
                    }
                } else {
                    if (args[1].equalsIgnoreCase("enderchest")) {
                        if (sender instanceof Player) {
                            ((Player) sender).openInventory(player.getEnderChest());
                        } else {
                            sender.sendMessage(String.format("%s's ender chest:\n%s", player.getName(), getInventoryList(player.getEnderChest().getContents())));
                        }
                    }
                    if (args[1].equalsIgnoreCase("armor")) {
                        sender.sendMessage(String.format("%s's armor:\n%s", player.getName(), getInventoryList(player.getInventory().getArmorContents())));
                    } else {
                        return false;
                    }
                }
            } else {
                sender.sendMessage(plugin.getString("player-offline", "{player}", args[0]));
            }
            return true;
        }
        return false;
    }

    private String getInventoryList(ItemStack[] stacks) {
        List<String> list = new ArrayList<>();
        for (ItemStack itemStack : stacks) {
            list.add(itemStack.getType().toString() + "x" + itemStack.getAmount() + "");
        }
        return list.toString();
    }
}
