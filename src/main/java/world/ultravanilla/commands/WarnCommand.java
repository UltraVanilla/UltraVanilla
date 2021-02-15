package world.ultravanilla.commands;

import world.ultravanilla.StaffAction;
import world.ultravanilla.UltraVanilla;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import world.ultravanilla.StaffAction;
import world.ultravanilla.UltraVanilla;

import java.util.ArrayList;
import java.util.List;

public class WarnCommand extends AdminCommand implements CommandExecutor, TabExecutor {

    public WarnCommand(UltraVanilla instance) {
        super(instance);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target != null) {
                String message = getArg(args, 2);
                String source = sender.getName();
                MsgCommand.msg(sender, target, message);
                plugin.ping(sender, target);
                StaffAction staffAction = new StaffAction(StaffAction.Type.WARN, message, source, target.getName());
                announce(staffAction);
            } else {
                sender.sendMessage(PLAYER + args[0] + COLOR + " is not online.");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        } else {
            return new ArrayList<>();
        }
    }
}
