package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public class VoteKickCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GOLD;
    private final Map<UUID, Set<UUID>> votes;

    public VoteKickCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
        votes = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            Player player = plugin.getServer().getPlayer(args[0]);
            if (player == null) {
                sendMessage(sender, "$noun%s %sis not online!", args[0]);
                return true;
            }
            if (!player.hasPermission("ultravanilla.command.votekick.excluded")) {
                if (addVote(player, (Player) sender)) {
                    sendMessage(sender, "You voted to kick $noun%s", player.getName());
                    for (Player participant : plugin.getServer().getOnlinePlayers()) {
                        if (participant != player && participant.hasPermission("ultravanilla.command.votekick.excluded")) {
                            participant.sendMessage(fmt(
                                    color + "$number%s%s/$number%s %speople are voting to kick $noun%s",
                                    votes.getOrDefault(player.getUniqueId(), new HashSet<>()).size(),
                                    color,
                                    (int) Math.floor(
                                            (double) plugin.getServer().getOnlinePlayers().size() *
                                                    (double) plugin.getConfig().getInt("votekick.percent-to-kick")),
                                    color,
                                    player.getName()));
                            if (hasVoted(player, participant)) {
                                participant.sendMessage(fmt(color + "Type &7/votekick " + player.getName() + color + " to votekick"));
                            }
                        }
                    }
                    checkVote(player);
                } else {
                    sendMessage(sender, "You already voted to kick $noun%s", player.getName());
                }
            } else {
                sendMessage(sender, "Cannot votekick $noun%s", player.getName());
            }
        }
        return true;
    }

    private boolean hasVoted(Player player, Player voter) {
        return votes.getOrDefault(player.getUniqueId(), new HashSet<>()).contains(voter.getUniqueId());
    }

    private boolean addVote(Player player, Player voter) {
        Set<UUID> totalVotes = votes.getOrDefault(player.getUniqueId(), new HashSet<>());
        if (!totalVotes.contains(voter.getUniqueId())) {
            totalVotes.add(voter.getUniqueId());
            votes.put(player.getUniqueId(), totalVotes);
            return true;
        }
        return false;
    }

    // yea this could use some VARIABLES but RAM is a problem rn so...
    private void checkVote(Player player) {
        if ((double) votes.get(player.getUniqueId()).size() >= plugin.getServer().getOnlinePlayers().size() * plugin.getConfig().getInt("votekick.percent-to-kick")) {
            player.kickPlayer(Palette.WRONG + "You have been votekicked from the server.");
            plugin.getServer().broadcastMessage(fmt("$noun%s %shas been votekicked from the server.", player.getName(), color));
            UltraVanilla.set(player, "votekicks", UltraVanilla.getConfig(player).getInt("votekicks", 0) + 1);
            votes.remove(player.getUniqueId());
            checkBan(player);
        }
    }

    // hardcoding everything cause this plugin is garbo, pls wait til i finish the 2.0 b4 criticizing ty
    private void checkBan(OfflinePlayer player) {
        if (UltraVanilla.getConfig(player).getInt("votekicks") >= plugin.getConfig().getInt("votekick.kicks-to-ban")) {
            player.banPlayer(Palette.WRONG + "You have been banned after being votekicked 3 times.");
            plugin.getServer().broadcastMessage(fmt("$noun%s %shas been banned from the server due to $number%s %svotekicks.",
                    color,
                    player.getName(),
                    plugin.getConfig().getInt("votekick.kicks-to-ban"),
                    color));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
