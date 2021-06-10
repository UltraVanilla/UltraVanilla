package world.ultravanilla.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import world.ultravanilla.AnarchyRegion;
import world.ultravanilla.UltraVanilla;
import world.ultravanilla.serializable.Position;

public class OldSpawnCommand extends UltraCommand implements CommandExecutor {

    public static final ChatColor COLOR = ChatColor.GREEN;

    public OldSpawnCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!AnarchyRegion.allowTeleport() && AnarchyRegion.inside(player.getLocation())) {
                    return true;
                }
                Position oldspawn = (Position) plugin.getConfig().get("oldspawn");
                if (oldspawn != null) {
                    player.teleport(oldspawn.getLocation());
                } else {
                    sender.sendMessage(format(command, "error.not-set"));
                }
            } else {
                sender.sendMessage(plugin.getString("player-only", "{action}", "set or go to the oldspawn"));
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("set")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("ultravanilla.command.oldspawn.set")) {
                        player.sendMessage(format(command, "message.set"));
                        plugin.getConfig().set("oldspawn", new Position(player.getLocation()));
                        plugin.saveConfig();
                    } else {
                        sender.sendMessage(plugin.getString("no-permission", "{action}", "set oldspawn"));
                    }
                } else {
                    sender.sendMessage(plugin.getString("player-only", "{action}", "set or go to the oldspawn"));
                }
            } else {
                Position oldspawn = (Position) plugin.getConfig().get("oldspawn");
                Player player = plugin.getServer().getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(plugin.getString("player-offline", "{player}", args[0]));
                    return true;
                }
                if (oldspawn != null) {
                    if (sender.hasPermission("ultravanilla.command.oldspawn.player")) {
                        player.teleport(oldspawn.getLocation());
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to teleport players to oldspawn.");
                    }
                } else {
                    sender.sendMessage(format(command, "error.not-set"));
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
