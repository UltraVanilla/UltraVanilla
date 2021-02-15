package world.ultravanilla.commands;

import world.ultravanilla.UltraVanilla;
import world.ultravanilla.reference.LegacyColors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import world.ultravanilla.reference.LegacyColors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class McolorCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.of("#add1c0");
    public static final ChatColor WRONG_COLOR = ChatColor.of("#d16c94");
    public static final ChatColor RIGHT_COLOR = ChatColor.of("#6cd1a2");

    public McolorCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 1) {
                if (args[0].equals("-disable")) {
                    Player player = (Player) sender;
                    UltraVanilla.set(player, "channels.direct-disabled", true);
                    sender.sendMessage(WRONG_COLOR + "Disabled" + COLOR + " custom conversation colors.");
                    return true;
                } else if (args[0].equals("-enable")) {
                    Player player = (Player) sender;
                    UltraVanilla.set(player, "channels.direct-disabled", false);
                    sender.sendMessage(RIGHT_COLOR + "Enabled" + COLOR + " custom conversation colors.");
                    return true;
                } else {
                    return false;
                }
            } else if (args.length == 2) {
                String newColorString = args[1];
                ChatColor newColor = ChatColor.of(newColorString);
                OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(args[0]);
                if (!(offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline())) {
                    sender.sendMessage(WRONG_COLOR + args[0] + COLOR + " has never played here before.");
                    return true;
                }
                UltraVanilla.set((Player) sender, "channels.direct." + offlinePlayer.getName(), newColorString);
                String name = offlinePlayer.isOnline() ? ((Player) offlinePlayer).getDisplayName() : offlinePlayer.getName();
                sender.sendMessage(COLOR + "Set your conversation color with " + RIGHT_COLOR + name + COLOR + " to " + newColor + newColorString);
                return true;
            }
            return false;
        } else {
            sender.sendMessage(WRONG_COLOR + "You cannot set your thread colors.");
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            for (OfflinePlayer offlinePlayer : plugin.getServer().getOfflinePlayers()) {
                suggestions.add(offlinePlayer.getName());
                suggestions.add("-disable");
                suggestions.add("-enable");
            }
        } else if (args.length == 2) {
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);

            if (!(args[0].matches("-(en|dis)able") || !(player.hasPlayedBefore() || player.isOnline()))) {
                suggestions = Arrays.asList(LegacyColors.listNames());
            }
        }
        return getSuggestions(suggestions, args);
    }
}
