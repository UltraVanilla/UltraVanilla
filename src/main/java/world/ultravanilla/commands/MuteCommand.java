package world.ultravanilla.commands;

import world.ultravanilla.UltraVanilla;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MuteCommand extends UltraCommand implements CommandExecutor, TabCompleter {

    public static final ChatColor COLOR = ChatColor.of("#c6add1");
    public static final ChatColor WRONG_COLOR = ChatColor.of("#b52f5d");
    public static final ChatColor RIGHT_COLOR = ChatColor.of("#7ed6c3");

    public MuteCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean unmute = command.getName().endsWith("unmute");
        boolean secret = command.getName().startsWith("s");
        if (args.length == 1) {
            Player player = plugin.getServer().getPlayer(args[0]);
            if (player != null) {
                if (!player.hasPermission("ultravanilla.command.mute.exclude") || unmute) {
                    if (!UltraVanilla.getPlayerConfig(player).getBoolean("muted")) {
                        if (!unmute) {
                            UltraVanilla.set(player, "muted", true);
                            sender.sendMessage(RIGHT_COLOR + player.getDisplayName() + COLOR + " has been muted.");
                            player.sendMessage(COLOR + "You have been " + WRONG_COLOR + "muted" + COLOR + ".");
                            if (!secret) {
                                for (Player p : plugin.getServer().getOnlinePlayers()) {
                                    if (p != sender && p != player) {
                                        p.sendMessage(WRONG_COLOR + player.getDisplayName() + COLOR + " has just been muted.");
                                    }
                                }
                            }
                        } else {
                            sender.sendMessage(WRONG_COLOR + player.getDisplayName() + COLOR + " is not muted.");
                        }
                    } else {
                        if (unmute) {
                            UltraVanilla.set(player, "muted", false);
                            sender.sendMessage(RIGHT_COLOR + player.getDisplayName() + COLOR + " has been un-muted.");
                            player.sendMessage(COLOR + "You have been " + RIGHT_COLOR + "un-muted" + COLOR + ".");
                            if (!secret) {
                                for (Player p : plugin.getServer().getOnlinePlayers()) {
                                    if (p != sender && p != player) {
                                        p.sendMessage(RIGHT_COLOR + player.getDisplayName() + COLOR + " has just been un-muted.");
                                    }
                                }
                            }
                        } else {
                            sender.sendMessage(WRONG_COLOR + player.getDisplayName() + COLOR + " is already muted.");
                        }
                    }
                } else {
                    sender.sendMessage(COLOR + "You cannot mute " + WRONG_COLOR + player.getDisplayName() + COLOR + ".");
                }
            } else {
                OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(args[0]);
                if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
                    sender.sendMessage(WRONG_COLOR + offlinePlayer.getName() + COLOR + " is offline.");
                } else {
                    sender.sendMessage(WRONG_COLOR + args[0] + COLOR + " has never played here before.");
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
            return null;
        }
        return suggestions;
    }
}
