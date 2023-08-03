package world.ultravanilla.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import world.ultravanilla.UltraVanilla;
import world.ultravanilla.reference.Palette;

import java.util.ArrayList;
import java.util.List;

public class RoleVariantCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GREEN;

    public RoleVariantCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getString("player-only", "{action}", "set role variants"));
            return true;
        }

        Player player = (Player) sender;
        String group = plugin.getRole(player);
        List<String> renames = plugin.getConfig().getStringList("rename-groups." + group);

        if (renames.size() == 0) {
            sender.sendMessage(plugin.getString("no-variants"));
            return true;
        }

        if (args.length == 0) {
            for (int i = 0; i < renames.size(); i++) {
                sendMessage(sender, i + ": " + renames.get(i));
            }
        } else {
            try {
                int variantId = Integer.parseInt(args[0]);
                if (variantId >= renames.size()) {
                    sender.sendMessage(plugin.getString("invalid-variant"));
                } else {
                    String roleText = renames.get(variantId);
                    UltraVanilla.set(player, "role-variant", variantId);
                    sender.sendMessage(plugin.getString("set-role-variant",
                        "{variant}", Palette.translate(roleText)));
                }
            } catch (NumberFormatException err) {
                sender.sendMessage(plugin.getString("invalid-variant"));
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            Player player = (Player) sender;
            String group = plugin.getRole(player);
            List<String> renames = plugin.getConfig().getStringList("rename-groups." + group);
            ArrayList<String> variantIds = new ArrayList<>();

            for (int i = 0; i < renames.size(); i++) {
                variantIds.add(Integer.toString(i));
            }

            return variantIds;
        } else {
            return new ArrayList<>();
        }
    }
}
