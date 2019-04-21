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

    public static final ChatColor COLOR = ChatColor.WHITE;

    public GamemodeCommand(Ultravanilla plugin) {
        super(plugin);
        color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        GameMode gameMode;

        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                gameMode = player.getGameMode() == GameMode.CREATIVE ? GameMode.SURVIVAL : GameMode.CREATIVE;
                setGameMode(sender, player, gameMode);
            } else {
                sender.sendMessage(playerOnly());
                return false;
            }
        } else {
            gameMode = getGameMode(args[0]);
            if (args.length == 1) {
                if (sender instanceof Player) {
                    setGameMode(sender, (Player) sender, gameMode);
                } else {
                    sender.sendMessage(playerOnly());
                }
            } else if (args.length == 2) {
                for (Player player : getPlayers(args[1])) {
                    if (player == null) {
                        sender.sendMessage(playerNotFound(args[1]));
                        return true;
                    } else {
                        setGameMode(sender, player, gameMode);
                    }
                }
            }
        }
        return true;
    }

    private void setGameMode(CommandSender sender, Player player, GameMode gameMode) {
        if (gameMode != null) {
            player.setGameMode(gameMode);
            sender.sendMessage(color + String.format("Set %s gamemode to %s", posessiveNoun(player.getName()), number(gameMode.name().toLowerCase())));
        } else {
            sender.sendMessage(wrong("Invalid gamemode specified"));
        }
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
            for (GameMode gameMode : GameMode.values()) {
                String name = gameMode.name().toLowerCase();
                list.add(name.substring(0, 1));
            }
        } else if (args.length == 2) {
            list = suggestPlayers();
        }
        return list;
    }
}
