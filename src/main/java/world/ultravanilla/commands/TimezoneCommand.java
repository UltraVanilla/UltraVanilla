package world.ultravanilla.commands;

import world.ultravanilla.UltraVanilla;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class TimezoneCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.LIGHT_PURPLE;

    public TimezoneCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                String timezone = UltraVanilla.getPlayerConfig(player.getUniqueId()).getString("timezone", "");
                if ((timezone == null || timezone.isEmpty())) {
                    sender.sendMessage(error(command, "not-set"));
                } else {
                    sender.sendMessage(message(command, "display", "{timezone}", timezone));
                }
            } else if (args.length == 1) {
                UltraVanilla.set(player, "timezone", args[0].toUpperCase());
                sender.sendMessage(message(command, "set", "{timezone}", args[0].toUpperCase()));
            } else {
                return false;
            }
        } else {
            sender.sendMessage(plugin.getString("player-only", "{action}", "set your timezone"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> timeZones = new ArrayList<>();
            for (String id : TimeZone.getAvailableIDs()) {
                if (id.length() <= 4) {
                    timeZones.add(id);
                }
            }
            return timeZones;
        }
        return null;
    }
}
