package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.stuff.Range;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RtpCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public RtpCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Experimental regex command
        Pattern pattern = Pattern.compile("([a-zA-Z0-9_,]+|!r[0-9]+)?(( [-0-9]+(\\.[0-9]+)?| \\^){6})( [a-zA-Z_]+)?");
        Matcher matcher = pattern.matcher(" " + String.join(" ", args));

        if (matcher.find()) {

            String playerString = matcher.group(1);
            String coordsString = matcher.group(2);
            String[] coordsArray = coordsString.trim().split(" ");
            String worldString = matcher.group(5);

            List<Player> players = new ArrayList<>();
            World world;

            if (playerString == null) {
                if (sender instanceof Player) {
                    players.add((Player) sender);
                } else {
                    sender.sendMessage("You must either specify players or be one to randomly tp.");
                    return true;
                }
            } else {

                if (playerString.startsWith("!r")) {
                    if (sender instanceof Player) {
                        int radius = Integer.parseInt(playerString.trim().substring(2));
                        sender.sendMessage("r:" + radius);
                        for (Entity entity : ((Player) sender).getNearbyEntities(radius, radius, radius)) {
                            if (entity instanceof Player) {
                                players.add((Player) entity);
                            }
                        }
                    } else {
                        sender.sendMessage("You must be a player to use !r<radius>.");
                        return true;
                    }
                } else {

                    for (String playerName : playerString.trim().split(",")) {
                        Player player = plugin.getServer().getPlayer(playerName);
                        if (player != null) {
                            players.add(player);
                        }
                    }
                }

                if (players.isEmpty()) {
                    sender.sendMessage("No online players matched your criteria.");
                    return true;
                }
            }

            for (Player player : players) {

                if (worldString == null) {
                    world = player.getWorld();
                } else {
                    world = plugin.getServer().getWorld(worldString.trim());
                    if (world == null) {
                        sender.sendMessage("That world doesn't exist.");
                        continue;
                    }
                }

                Range xRange = new Range(Float.parseFloat(coordsArray[0]), Float.parseFloat(coordsArray[3]));
                Range zRange = new Range(Float.parseFloat(coordsArray[2]), Float.parseFloat(coordsArray[5]));

                float x = xRange.getRandom();
                float z = zRange.getRandom();
                float y;

                if (coordsArray[1].equals("^") || coordsArray[4].equals("^")) {
                    y = world.getHighestBlockYAt((int) x, (int) z) + 1;
                } else {
                    Range yRange = new Range(Float.parseFloat(coordsArray[1]), Float.parseFloat(coordsArray[4]));
                    y = yRange.getRandom();
                }

                sender.sendMessage(String.format("Teleported %s to %.0f %.0f %.0f in %s.", player.getName(), x, y, z, world.getName()));

                player.teleport(new Location(world, x, y, z));
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
        } else if (args.length == 3 || args.length == 6) {
            suggestions.add("^");
        }

        if (args.length == 7 && args[0].matches("[0-9]+(.[0-9]+)?") || args.length == 8) {
            for (World world : plugin.getServer().getWorlds()) {
                suggestions.add(world.getName());
            }
        }
        return getSuggestions(suggestions, args);
    }
}
