package world.ultravanilla.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import world.ultravanilla.UltraVanilla;

import java.util.ArrayList;
import java.util.List;

public class SuggestCommand extends UltraCommand implements TabExecutor {

    public static final ChatColor COLOR = ChatColor.of("#12e86f");
    public static final String PLAYER_PLACEHOLDER = "[player]";
    public static final String RANDOM_PLAYER_PLACEHOLDER = "[random player]";
    public static final String RANDOM_ONLINE_PLAYER_PLACEHOLDER = "[random online player]";
    public static final String THEM_PLACEHOLDER = "[them]";

    public SuggestCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            String message = getArg(args, 2);
            if (args[0].equalsIgnoreCase("suicide")) {
                plugin.addToStorage("suggestions.suicide", message);
            }
            sender.sendMessage(COLOR + "Suggestion added.");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length >= 2) {
            String message = getArg(args, 2);
            if (!message.toLowerCase().contains(PLAYER_PLACEHOLDER.toLowerCase())) {
                suggestions.add(PLAYER_PLACEHOLDER);
                return getSuggestions(suggestions, args);
            }
        }
        return null;
    }
}
