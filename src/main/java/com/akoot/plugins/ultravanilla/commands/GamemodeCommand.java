package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GamemodeCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public GamemodeCommand(Ultravanilla plugin) {
        super(plugin);
        this.color = ChatColor.GRAY;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;
        GameMode gameMode = null;
        String playerName = "your";
        String s = "";

        if (args.length == 0) {
            if (sender instanceof Player) {
                player = (Player) sender;
                gameMode = player.getGameMode() == GameMode.CREATIVE ? GameMode.SURVIVAL : GameMode.CREATIVE;
            } else {
                sender.sendMessage(playerOnly());
                return false;
            }
        } else {
            gameMode = getGameMode(args[0]);
            if (args.length == 1) {
                if (sender instanceof Player) {
                    player = (Player) sender;
                } else {
                    sender.sendMessage(playerOnly());
                    return false;
                }
            } else if (args.length == 2) {
                playerName = args[1];
                s = "'s";
                player = plugin.getServer().getPlayer(playerName);
                if (player == null) {
                    sender.sendMessage(playerNotFound(playerName));
                    return true;
                } else {
                    playerName = player.getName();
                }
            }
        }
        player.setGameMode(gameMode);
        sender.sendMessage(color + String.format("Set %s gamemode to %s", noun(playerName + s), number(gameMode.name().toLowerCase())));
        return true;
    }

    private GameMode getGameMode(String gameModeString) {
        gameModeString = gameModeString.toLowerCase();
        if (gameModeString.startsWith("c") || gameModeString.equals("1")) {
            return GameMode.CREATIVE;
        } else if (gameModeString.startsWith("sp") || gameModeString.equals("3")) {
            return GameMode.SPECTATOR;
        } else if (gameModeString.startsWith("s") || gameModeString.equals("0")) {
            return GameMode.SURVIVAL;
        } else if (gameModeString.startsWith("a") || gameModeString.equals("2")) {
            return GameMode.ADVENTURE;
        }
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        // Initialize the list as empty
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            if (args[0].length() < 1) {
                for (GameMode gameMode : GameMode.values()) {
                    String name = gameMode.name().toLowerCase();
                    list.add(name.substring(0, 1));
                    list.add(name);
                }
            } else {
                for (GameMode gameMode : GameMode.values()) {
                    String name = gameMode.name().toLowerCase();
                    if (name.startsWith(args[0])) {
                        list.add(name.substring(0, 1));
                        list.add(name);
                    }
                }
            }
        } else if (args.length == 2) {
            return null;
        }
        return list;
    }
}
