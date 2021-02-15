package world.ultravanilla.commands;

import world.ultravanilla.UltraVanilla;
import world.ultravanilla.reference.Palette;
import world.ultravanilla.stuff.StringUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import world.ultravanilla.UltraVanilla;
import world.ultravanilla.reference.Palette;
import world.ultravanilla.stuff.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayTimeCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.of("#8DF6C8");

    public PlayTimeCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    private static String getPlayTime(OfflinePlayer player) {
        return StringUtil.getTimeString(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20L * 1000L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer player;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getString("player-only", "{action}", "check your playtime"));
                return true;
            }
            player = (Player) sender;
        } else if (args.length == 1) {
            player = plugin.getServer().getOfflinePlayer(args[0]);
            if (!(player.hasPlayedBefore() || player.isOnline())) {
                sender.sendMessage(plugin.getString("player-unknown", "{player}", args[0]));
                return true;
            }
        } else {
            return false;
        }

        sender.sendMessage(Palette.NOUN + player.getName() + COLOR + " has played for " + Palette.NUMBER + getPlayTime(player));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        Arrays.stream(plugin.getServer().getOfflinePlayers()).forEach(p -> suggestions.add(p.getName()));
        return getSuggestions(suggestions, args);
    }
}
