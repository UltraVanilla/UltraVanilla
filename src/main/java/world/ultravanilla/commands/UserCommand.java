package world.ultravanilla.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import world.ultravanilla.UltraVanilla;

import java.util.ArrayList;
import java.util.List;

public class UserCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.DARK_GREEN;

    public UserCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Allow spaces with quotes
        args = refinedArgs(args);

        // If the sender is the user, send their own info
        // user
        if (args.length == 0) {
            if (sender instanceof Player) {
                sendInfo(sender, (Player) sender);
            } else {
                sender.sendMessage(plugin.getString("player-only", "{action}", "check your own data"));
            }
        }

        // If the sender specifies 1 user, send the specified user's info
        // user notch
        else if (args.length == 1) {
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
            if (player.hasPlayedBefore() || player.isOnline()) {
                sendInfo(sender, player);
            } else {
                sender.sendMessage(plugin.getString("player-unknown", "{player}", args[0]));
            }
        }

        // Get specified user's value
        // user notch get namecolor
        // user notch clear namecolor
        else if (args.length == 3) {

        }

        // Set specified user's value
        // user notch set namecolor AQUA
        else if (args.length == 4) {

        } else {
            return false;
        }
        return true;
    }

    private void sendInfo(CommandSender sender, OfflinePlayer player) {
        YamlConfiguration config = UltraVanilla.getPlayerConfig(player);
        List<String> info = new ArrayList<>();
        for (String value : config.getKeys(true)) {
            if (!(config.get(value) instanceof MemorySection)) {
                info.add(color + value + ": " + ChatColor.RESET + config.get(value) + "");
            }
        }
        info.forEach(sender::sendMessage);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
