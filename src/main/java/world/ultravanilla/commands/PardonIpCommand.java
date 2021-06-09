package world.ultravanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import world.ultravanilla.StaffAction;
import world.ultravanilla.UltraVanilla;

import java.util.ArrayList;
import java.util.List;

public class PardonIpCommand extends AdminCommand implements CommandExecutor, TabExecutor {

    public PardonIpCommand(UltraVanilla instance) {
        super(instance);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (String ip : args) {
            if (plugin.getServer().getIPBans().contains(ip)) {
                plugin.getServer().unbanIP(ip);
                StaffAction staffAction = new StaffAction(StaffAction.Type.PARDON_IP, "", sender.getName(), ip);
                announce(staffAction);
            } else {
                sender.sendMessage(COLOR + "IP " + PLAYER + ip + COLOR + " is not banned.");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>(plugin.getServer().getIPBans());
    }
}
