package world.ultravanilla.commands;

import world.ultravanilla.StaffAction;
import world.ultravanilla.UltraVanilla;
import org.bukkit.BanList;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import world.ultravanilla.StaffAction;

import java.util.ArrayList;
import java.util.List;

public class PardonCommand extends AdminCommand implements CommandExecutor, TabExecutor {

    public PardonCommand(UltraVanilla instance) {
        super(instance);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (String name : args) {
            OfflinePlayer target = plugin.getServer().getOfflinePlayer(name);
            if (target.isBanned()) {
                plugin.getServer().getBanList(BanList.Type.NAME).pardon(target.getUniqueId().toString());
                StaffAction staffAction = new StaffAction(StaffAction.Type.PARDON, "", sender.getName(), target.getName());
                announce(staffAction);
            } else {
                sender.sendMessage(PLAYER + target.getName() + COLOR + " is not banned.");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> bannedPlayers = new ArrayList<>();
        for (OfflinePlayer bannedPlayer : plugin.getServer().getBannedPlayers()) {
            bannedPlayers.add(bannedPlayer.getName());
        }
        return bannedPlayers;
    }
}
