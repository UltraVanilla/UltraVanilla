package world.ultravanilla.commands;

import world.ultravanilla.UltraVanilla;
import world.ultravanilla.serializable.Title;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import world.ultravanilla.serializable.Title;

import java.util.List;

public class TitlesCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public TitlesCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (sender instanceof Player) {
                    sender.sendMessage(format(command, "list.titles", "{title}", "title"));
                    sender.sendMessage(Title.getUnlockedTitleNames((Player) sender).toString());
                    if (sender.hasPermission("ultravanilla.admin")) {
                        sender.sendMessage(format(command, "list.all-titles", "{title}", "title"));
                        sender.sendMessage(Title.getTitles().toString());
                    }
                } else {
                    sender.sendMessage(plugin.getString("player-only", "{action}", "list your roles"));
                }
            } else {
                return false;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                if (sender instanceof Player) {
                    setTitle(sender, (Player) sender, args[1], command);
                } else {
                    sender.sendMessage(plugin.getString("player-only", "{action}", "list your roles"));
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
                if (!(player.hasPlayedBefore() || player.isOnline())) {
                    sender.sendMessage(plugin.getString("player-unknown"));
                    return true;
                }
                setTitle(sender, player, args[2], command);
            }
        }
        return true;
    }

    private void setTitle(CommandSender sender, OfflinePlayer player, String title, Command command) {
        if (Title.titleExists(title)) {
            if (sender.hasPermission("ultravanilla.admin") || Title.hasTitleUnlocked(player, title)) {
                sender.sendMessage(format(command, "title.set", "{title}", title));
                Title.setDisplayTitle(player, title);
            } else {
                sender.sendMessage(error(command, "title-locked", "{title}", title));
            }
        } else {
            sender.sendMessage(error(command, "title-unknown", "{title}", title));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
