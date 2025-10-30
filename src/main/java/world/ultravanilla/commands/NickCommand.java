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

public class NickCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public NickCommand(UltraVanilla instance) {
        super(instance);
        color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (args[0].equals(player.getName())) {
                        UltraVanilla.set(player, "display-name", null);
                        UltraVanilla.updateDisplayName(player);
                        sender.sendMessage(format(command, "message.clear.self"));
                    } else {
                        String newName = Palette.translate(args[0]);
                        UltraVanilla.set(player, "display-name", newName);
                        UltraVanilla.updateDisplayName(player);
                        sender.sendMessage(format(command, "message.set.self", "{name}", player.getDisplayName()));
                    }
                } else {
                    sender.sendMessage(plugin.getString("player-only", "{action}", "have a nickname"));
                }
            } else if (args.length == 2) {
                if (sender.hasPermission("ultravanilla.command.nick.others")) {
                    Player target = plugin.getServer().getPlayer(args[0]);
                    String newName = Palette.translate(args[1]);
                    if (target != null) {
                        if (args[1].equals(target.getName())) {
                            UltraVanilla.set(target, "display-name", null);
                            UltraVanilla.updateDisplayName(target);
                            sender.sendMessage(format(command, "message.clear.other", "{player}", target.getName(), "{player's}", possessive(target.getName())));
                        } else {
                            UltraVanilla.set(target, "display-name", newName);
                            UltraVanilla.updateDisplayName(target);
                            sender.sendMessage(format(command, "message.set.other", "{player}", target.getName(), "{player's}", possessive(target.getName()), "{name}", target.getDisplayName()));
                        }
                    } else {
                        sender.sendMessage(plugin.getString("player-offline", "{player}", args[0]));
                    }
                } else {
                    sender.sendMessage(plugin.getString("no-permission", "{action}", "change other people's nicknames"));
                }
            }
            return true;
        }
        return false;
    }

   @Override
public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    List<String> suggestions = new ArrayList<>();

    if (args.length == 1) {
        if (sender.hasPermission("ultravanilla.command.nick.others")) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    suggestions.add(player.getName());
                }
            }
        }
        return suggestions;
    }

    if (args.length == 2) {
        return suggestions;
    }

    return suggestions;
}
