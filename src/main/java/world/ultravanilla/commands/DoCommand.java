package world.ultravanilla.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import world.ultravanilla.UltraVanilla;
import world.ultravanilla.stuff.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class DoCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public DoCommand(UltraVanilla instance) {
        super(instance);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            args = refinedArgs(args);
            long timer = 0;
            if (args[0].startsWith("delay:")) {
                System.out.println(args[0].substring(args[0].indexOf(":") + 1));
                timer = (long) (StringUtil.getSeconds(args[0].substring(args[0].indexOf(":") + 1)) * 20.0);
                System.out.println(timer);
            }
            if (timer > 0) {
                for (int i = 1, argsLength = args.length; i < argsLength; i++) {
                    String arg = args[i];
                    Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.dispatchCommand(sender, arg), timer * (i - 1));
                }
            } else {
                for (String arg : args) {
                    Bukkit.dispatchCommand(sender, arg);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            String arg = args[0];
            if (!arg.startsWith("\"") && !arg.startsWith("'")) {
                String suggestion = "delay:";
                if (!arg.startsWith(suggestion)) {
                    suggestions.add(suggestion);
                }
            }
        }
        return suggestions;
    }
}
