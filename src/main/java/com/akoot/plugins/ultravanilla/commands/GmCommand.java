package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GmCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public GmCommand(UltraVanilla plugin) {
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
                sender.sendMessage(format(command, "message.set.self", "{gamemode}", gameMode.name()));
                player.setGameMode(gameMode);
            } else {
                sender.sendMessage(plugin.getString("player-only", "{action}", "change your gamemode"));
                return false;
            }
        } else {
            gameMode = getGameMode(args[0]);
            if (gameMode != null) {
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (player.getGameMode() != gameMode) {
                            sender.sendMessage(format(command, "message.set.self", "{gamemode}", gameMode.name()));
                            player.setGameMode(gameMode);
                        } else {
                            sender.sendMessage(format(command, "error.already-in-gamemode.self", "{gamemode}", gameMode.name()));
                        }
                    } else {
                        sender.sendMessage(plugin.getString("player-only", "{action}", "change your gamemode"));
                    }
                } else if (args.length == 2) {
                    for (Player player : getPlayers(args[1])) {
                        if (player != null) {
                            if (player.getGameMode() != gameMode) {
                                sender.sendMessage(format(command, "message.set.other", "{player}", player.getName(), "{player's}", posessive(player.getName()), "{gamemode}", gameMode.name()));
                                player.setGameMode(gameMode);
                            } else {
                                sender.sendMessage(format(command, "error.already-in-gamemode.other", "{player}", player.getName(), "{gamemode}", gameMode.name()));

                            }
                        } else {
                            sender.sendMessage(plugin.getString("player-offline", "{player}", args[1]));
                        }
                    }
                } else {
                    return false;
                }
            } else {
                sender.sendMessage(format(command, "error.invalid-gamemode", "{gamemode}", args[0]));
            }
        }
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
            for (GameMode gameMode : GameMode.values()) {
                String name = gameMode.name().toLowerCase();
                list.add(name.substring(0, 1));
            }
        } else if (args.length == 2) {
            return null;
        }
        return list;
    }
}
