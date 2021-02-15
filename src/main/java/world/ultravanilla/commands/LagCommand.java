package world.ultravanilla.commands;

import world.ultravanilla.UltraVanilla;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LagCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GRAY;

    public LagCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int i = 0;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("average")) {
                sender.sendMessage(plugin.getTitle(format(command, "format.list.title.average"), color));
                i = 2;
            } else if (args[0].equalsIgnoreCase("median")) {
                sender.sendMessage(plugin.getTitle(format(command, "format.list.title.median"), color));
                i = 1;
            }
        } else {
            sender.sendMessage(plugin.getTitle(format(command, "format.list.title.current"), color));
        }
        double tps = Bukkit.getServer().getTPS()[i];
        double percent = (1.0 - (tps / 20.0)) * 100.0;
        if (percent < 0) {
            percent = 0.0;
        }
        String percentString = String.format("%.1f", percent);
        String tpsString = String.format("%.1f", tps);
        if (sender instanceof Player) {
            String ping = ((Player) sender).spigot().getPing() + "";
            sender.sendMessage(format(command, "format.list.item.ping", "{ping}", ping));
        }
        sender.sendMessage(format(command, "format.list.item.tps", "{tps}", tpsString));
        sender.sendMessage(format(command, "format.list.item.percent", "{percent}", percentString));

        ;
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("average");
            suggestions.add("median");
        }
        return suggestions;
    }
}
