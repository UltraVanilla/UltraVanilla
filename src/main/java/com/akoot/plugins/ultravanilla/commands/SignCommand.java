package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.LegacyColors;
import com.akoot.plugins.ultravanilla.reference.Palette;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignCommand extends UltraCommand implements TabExecutor, Listener {

    public static final ChatColor COLOR = ChatColor.of("#d1bcad");
    public static final ChatColor WRONG_COLOR = ChatColor.of("#ce7b91");
    public static final ChatColor RIGHT_COLOR = ChatColor.of("#7bcec6");
    public static final Material MULTI_DYE = Material.BLACK_DYE;

    public static final String SPLIT_LINES = "[^\\\\]\\|";

    public SignCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    public static boolean canColor(Player player) {
        return player.hasPermission("ultravanilla.sign.color");
    }

    public static boolean isValid(String arg) {
        int size = arg.split(SPLIT_LINES).length;
        return size > 0 && size <= 4;
    }

    public static void rewriteSign(Sign sign, Player player, String arg) {
        String[] lines = new String[4];
        String[] gotLines = arg.replaceAll("\\\\\\|", "\n").split("\\|");
        for (int i = 0; i < lines.length; i++) {
            if (i < gotLines.length) {
                String line = gotLines[i].trim().replaceAll("\n", "|");
                if (canColor(player)) {
                    line = Palette.translate(line);
                }
                sign.setLine(i, line);
            } else {
                sign.setLine(i, "");
            }
        }
        sign.update();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent e) {
        Player player = e.getPlayer();
        if (!canColor(player)) return;

        String[] lines = e.getLines();

        for (int i = 0; i < lines.length; i++) {
            String oldLine = lines[i];
            String newLine = Palette.translate(oldLine);
            e.setLine(i, newLine);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Block block = player.getTargetBlock(3);
            Sign sign;
            if (block != null && block.getState() instanceof Sign) {
                sign = (Sign) block.getState();
            } else {
                sender.sendMessage(COLOR + "You must be looking at a " + WRONG_COLOR + "sign" + COLOR + " to execute this command!");
                return true;
            }
            if (args.length == 1) {
                String arg1 = args[0];
                if (arg1.equals("clear")) {
                    for (int i = 0; i < sign.getLines().length; i++) {
                        sign.setLine(i, "");
                    }
                    sign.update();
                    sender.sendMessage(RIGHT_COLOR + "Cleared" + COLOR + " this sign.");
                    return true;
                } else if (arg1.equalsIgnoreCase("edit")) {

                    for (String line : sign.getLines()) {
                        System.out.println(line.replaceAll("§", "&"));
                    }
                    String lines = String.join("\n", sign.getLines())
                            .replaceAll("\\|", "\\\\|")
                            .replaceAll("\n", " | ");
                    lines = Palette.untranslate(lines);

                    TextComponent editButton = new TextComponent("Click here to rewrite this sign.");
                    editButton.setColor(RIGHT_COLOR);
                    editButton.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sign rewrite " + lines));
                    player.sendMessage(editButton);
                    return true;
                } else if (arg1.equalsIgnoreCase("open")) {
                    player.openSign(sign);
                    return true;
                }
            } else if (args.length == 2) {
                String arg1 = args[0];
                String arg2 = args[1];
                if (arg1.equals("color")) {
                    if (canColor(player)) {
                        try {
                            ChatColor color = ChatColor.of(arg2);
                            for (int i = 0; i < 4; i++) {
                                String line = sign.getLine(i);
                                if (!line.isEmpty()) {
                                    sign.setLine(i, color + ChatColor.stripColor(line));
                                }
                            }
                            sign.update();
                            sender.sendMessage(RIGHT_COLOR + "Colored" + COLOR + " this sign " + color + arg2 + COLOR + ".");
                        } catch (IllegalArgumentException e) {
                            sender.sendMessage(WRONG_COLOR + arg2 + COLOR + " is not a valid color.");
                        }
                    } else {
                        sender.sendMessage(UltraVanilla.getInstance().getString("no-permission", "{action}", "color a sign via command."));
                    }
                    return true;
                } else if (arg1.equals("rewrite")) {
                    if (isValid(arg2)) {
                        rewriteSign(sign, player, arg2);
                        sender.sendMessage(RIGHT_COLOR + "Successfully " + COLOR + "rewrote this sign!");
                    } else {
                        sender.sendMessage(WRONG_COLOR + "Invalid number of lines.");
                    }
                    return true;
                }
            } else {
                if (args.length > 2) {
                    String arg1 = args[0];
                    String arg;
                    if (arg1.equals("rewrite")) {
                        arg = getArg(args, 2);
                        if (isValid(arg)) {
                            rewriteSign(sign, player, arg);
                            sender.sendMessage(RIGHT_COLOR + "Successfully " + COLOR + "rewrote this sign!");
                        } else {
                            sender.sendMessage(WRONG_COLOR + "Invalid number of lines.");
                        }
                        return true;
                    } else if (arg1.equals("setline")) {
                        String arg2 = args[1];
                        arg = getArg(args, 3);
                        if (canColor(player)) {
                            arg = Palette.translate(arg);
                        }
                        try {
                            int lineNumber = Integer.parseInt(arg2);
                            if (lineNumber > 0 && lineNumber <= 4) {
                                String lastLine = sign.getLine(lineNumber - 1);
                                sign.setLine(lineNumber - 1, arg);
                                sign.update();
                                if (lastLine.isEmpty()) {
                                    sender.sendMessage(String.format("%sSet %sline %d %sto %s%s", COLOR, RIGHT_COLOR, lineNumber, COLOR, ChatColor.RESET, arg));
                                } else {
                                    sender.sendMessage(String.format("%sChanged %sline %d %sfrom %s%s %sto %s%s", COLOR, RIGHT_COLOR, lineNumber, COLOR, ChatColor.RESET, lastLine, COLOR, ChatColor.RESET, arg));
                                }

                            } else {
                                sender.sendMessage(String.format("%s%s %sneeds to be a number %sbetween 1 and 4", WRONG_COLOR, arg, COLOR, RIGHT_COLOR));
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(String.format("%s%s %sis not a valid number.", WRONG_COLOR, arg2, COLOR));
                        }
                        return true;
                    } else if (arg1.equals("colorline")) {
                        if (canColor(player)) {
                            String arg2 = args[1];
                            String arg3 = args[2];
                            try {
                                ChatColor color = ChatColor.of(arg3);
                                int lineNumber = Integer.parseInt(arg2);
                                if (lineNumber > 0 && lineNumber <= 4) {
                                    sign.setLine(lineNumber - 1, color + ChatColor.stripColor(sign.getLine(lineNumber - 1)));
                                    sign.update();
                                    sender.sendMessage(RIGHT_COLOR + "Colored" + COLOR + " line " + RIGHT_COLOR + lineNumber + " " + color + arg3 + COLOR + ".");
                                } else {
                                    sender.sendMessage(WRONG_COLOR + arg2 + COLOR + " needs to be a number " + RIGHT_COLOR + "between 1 and 4");
                                }
                            } catch (NumberFormatException e) {
                                sender.sendMessage(WRONG_COLOR + arg2 + COLOR + " is not a valid number.");
                            } catch (IllegalArgumentException e2) {
                                sender.sendMessage(WRONG_COLOR + arg3 + COLOR + " is not a valid color.");
                            }
                        } else {
                            sender.sendMessage(UltraVanilla.getInstance().getString("no-permission", "{action}", "color a sign via command."));
                        }
                    }
                    return true;
                }
            }
            return false;
        } else {
            sender.sendMessage(COLOR + "You must be a player to use " + WRONG_COLOR + "/sign" + COLOR + ".");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (sender instanceof Player) {
            boolean canColor = canColor((Player) sender);
            if (args.length == 1) {
                suggestions.add("edit");
                suggestions.add("rewrite");
                suggestions.add("setline");
                if (canColor) {
                    suggestions.add("color");
                    suggestions.add("colorline");
                }
                suggestions.add("clear");
                suggestions.add("open");
            } else if (args.length == 2) {
                String arg1 = args[0];
                if (arg1.matches("setline|colorline")) {
                    suggestions.add("1");
                    suggestions.add("2");
                    suggestions.add("3");
                    suggestions.add("4");
                } else if (canColor && arg1.matches("color")) {
                    suggestions.addAll(Arrays.asList(LegacyColors.listNames()));
                }
            } else if (args.length == 3) {
                String arg1 = args[0];
                if (canColor && arg1.matches("colorline")) {
                    suggestions.addAll(Arrays.asList(LegacyColors.listNames()));
                } else if (arg1.matches("setline")) {
                    Block block = ((Player) sender).getTargetBlock(3);
                    if (block != null && block.getState() instanceof Sign) {
                        String line = ((Sign) block.getState()).getLine(Integer.parseInt(args[1]) - 1);
                        suggestions.add(Palette.untranslate(line));
                    }
                }
            }
        }
        return getSuggestions(suggestions, args);
    }
}
