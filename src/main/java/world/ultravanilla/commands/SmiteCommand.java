package world.ultravanilla.commands;

import world.ultravanilla.UltraVanilla;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import world.ultravanilla.UltraVanilla;

import java.util.ArrayList;
import java.util.List;

public class SmiteCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public SmiteCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            List<Player> players = getPlayers(args[0]);
            String message = args.length > 1 ? getArg(args, 2) : "";
            for (Player player : players) {
                player.getWorld().strikeLightning(player.getLocation());
                if (!message.isEmpty()) {
                    player.sendMessage(ChatColor.GRAY + message);
                }
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            return new ArrayList<>();
        }
        return null;
    }
}
