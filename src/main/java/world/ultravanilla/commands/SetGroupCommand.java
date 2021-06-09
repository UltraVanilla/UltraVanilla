package world.ultravanilla.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.track.Track;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import world.ultravanilla.UltraVanilla;
import world.ultravanilla.reference.Palette;

import java.util.ArrayList;
import java.util.List;

public class SetGroupCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.of("#79ea75");
    public static final ChatColor WRONG = ChatColor.of("#ea7586");
    private final LuckPerms luckPerms;

    public SetGroupCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
        this.luckPerms = instance.luckPerms();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            Player player = plugin.getServer().getPlayer(args[0]);
            String group = args[1];
            if (player != null) {
                Track staffTrack = luckPerms.getTrackManager().getTrack("staff");
                if (staffTrack == null || !staffTrack.containsGroup(group)) {
                    plugin.setGroup(player, group);
                    sender.sendMessage(COLOR + "Set " + Palette.NOUN + player.getName() + COLOR + " 's group to " + Palette.OBJECT + group);
                } else {
                    sender.sendMessage(WRONG + "You cannot edit staff groups.");
                }
            } else {
                sender.sendMessage(plugin.getString("player-unknown", "{player}", args[0]));
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        } else if (args.length == 2) {
            List<String> groups = new ArrayList<>();
            for (Group group : luckPerms.getGroupManager().getLoadedGroups()) {
                groups.add(group.getName());
            }
            return getSuggestions(groups, args);
        } else {
            return new ArrayList<>();
        }
    }
}
