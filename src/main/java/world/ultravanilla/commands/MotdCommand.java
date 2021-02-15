package world.ultravanilla.commands;

import world.ultravanilla.UltraVanilla;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class MotdCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GREEN;

    public MotdCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.motd().replace("%player", sender.getName()));
        } else {
            if (sender.hasPermission("ultravanilla.command.motd.set")) {
                String motd = getArg(args);
                plugin.setMOTD(motd);
                sender.sendMessage(format(command, "message.set", "{motd}", motd));
            } else {
                sender.sendMessage(plugin.getString("no-permission", "{action}", "set the MOTD"));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender.hasPermission("ultravanilla.command.motd.set")) {
            return plugin.getConfig().getStringList("motd.strings");
        }
        return new ArrayList<>();
    }
}
