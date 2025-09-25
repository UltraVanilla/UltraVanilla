package world.ultravanilla.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import world.ultravanilla.StaffAction;
import world.ultravanilla.UltraVanilla;

public class KickCommand extends AdminCommand implements CommandExecutor, TabExecutor {

    public KickCommand(UltraVanilla instance) {
        super(instance);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target != null) {
                String reason = getArg(args, 2);
                String source = sender.getName();
                target.kickPlayer(ChatColor.RED + reason + ChatColor.RESET);
                StaffAction staffAction = new StaffAction(StaffAction.Type.KICK, reason, source, target.getName());
                announce(staffAction);

                plugin.getServer().broadcast(
                    target.getName() +
                    COLOR + " has been kicked: " +
                    ChatColor.RESET + reason +
                    COLOR + ".",
                    "ultravanilla.seebans"
                );
            } else {
                sender.sendMessage(PLAYER + args[0] + COLOR + " is not online.");
            }
            return true;
        }
        return false;
    }
}
