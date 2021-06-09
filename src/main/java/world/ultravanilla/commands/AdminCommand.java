package world.ultravanilla.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import world.ultravanilla.StaffAction;
import world.ultravanilla.UltraVanilla;
import world.ultravanilla.stuff.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AdminCommand extends UltraCommand implements TabExecutor {

    public static final ChatColor COLOR = ChatColor.of("#ed521e");
    public static final ChatColor PLAYER = ChatColor.of("#f7a204");
    public static final ChatColor TIME = ChatColor.of("#f7d31d");

    protected List<String> suggestions;

    public AdminCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
        suggestions = new ArrayList<>();
        suggestions.add("griefing");
        suggestions.add("stealing");
        suggestions.add("non-consensual pvp");
        suggestions.add("spamming");
        suggestions.add("being inappropriate");
        suggestions.add("hacking");
    }

    protected void announce(StaffAction staffAction) {
        plugin.getStaffActionsRecord().log(staffAction);
        long expires = staffAction.getExpires();
        String description = staffAction.getDescription();
        String message =
                PLAYER + staffAction.getSource() + COLOR +
                        " " + staffAction.getType().getVerb() + " " +
                        PLAYER + staffAction.getTarget() + COLOR +
                        (expires > 0 ? " for " + TIME + StringUtil.getTimeString(expires - staffAction.getCreated()) + COLOR : "") +
                        (!description.isEmpty() ? ": " + ChatColor.RESET + description + COLOR : "") + COLOR + ".";
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("ultravanilla.mod")) {
                player.sendMessage(message);
            }
        }
        plugin.getLogger().info(ChatColor.stripColor(message));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        } else if (args.length == 2) {
            return getSuggestions(suggestions, args);
        }
        return new ArrayList<>();
    }

}
