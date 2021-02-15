
package world.ultravanilla.commands;

import world.ultravanilla.UltraVanilla;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.track.Track;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class PromoteCommand extends UltraCommand implements TabExecutor {
    public static final ChatColor COLOR = ChatColor.of("#79ea75");
    private final LuckPerms luckPerms;

    public PromoteCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
        luckPerms = instance.luckPerms();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (String playerName : args) {

            Player player = plugin.getServer().getPlayer(playerName);

            if (player != null) {
                User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);

                if (!plugin.hasRightRole(player)) {
                    Track playerTrack = luckPerms.getTrackManager().getTrack("player");
                    Group currentGroup = luckPerms.getGroupManager().getGroup(user.getPrimaryGroup());
                    if (playerTrack != null && currentGroup != null) {
                        String nextGroup = playerTrack.getNext(currentGroup);
                        if (nextGroup != null) {
                            playerTrack.promote(user, ImmutableContextSet.empty());
                            luckPerms.getUserManager().saveUser(user);
                            sender.sendMessage(format(command, "message.promoted.sender", "{player}", player.getName(), "{group}", plugin.getColoredRole(nextGroup)));
                            player.sendMessage(format(command, "message.promoted.player", "{group}", plugin.getColoredRole(nextGroup)));
                        } else {
                            sender.sendMessage(format(command, "error.fail", "{player}", playerName));
                        }
                    } else {
                        sender.sendMessage(format(command, "error.fail", "{player}", playerName));
                    }
                } else {
                    sender.sendMessage(format(command, "error.already-promoted", "{player}", playerName, "{group}", plugin.getColoredRole(user.getPrimaryGroup())));
                }
            }
        }

        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
