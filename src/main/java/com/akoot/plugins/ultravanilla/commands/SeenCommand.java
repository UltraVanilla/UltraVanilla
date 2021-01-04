package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import com.akoot.plugins.ultravanilla.serializable.Position;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SeenCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.of("#fce192");

    public SeenCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    private void sendLastSeen(CommandSender sender, OfflinePlayer player, TimeZone timeZone) {
        long lastSeen = player.getFirstPlayed();
        sender.sendMessage(Palette.NOUN + player.getName() + COLOR + " was last seen on " + Palette.OBJECT + getDate(lastSeen, timeZone));
        if (UltraVanilla.isStaff(sender)) {
            sendPromotionInfo(sender, player, timeZone);
            sendLocationInfo(sender, player);
        }
    }

    private void sendFirstJoined(CommandSender sender, OfflinePlayer player, TimeZone timeZone) {
        long firstJoined = player.getFirstPlayed();
        sender.sendMessage(Palette.NOUN + player.getName() + COLOR + " first joined on " + Palette.OBJECT + getDate(firstJoined, timeZone));
    }

    private void sendPromotionInfo(CommandSender sender, OfflinePlayer player, TimeZone timeZone) {
        plugin.async(() -> {
            sender.sendMessage(COLOR + "Last promoted: " + Palette.OBJECT + getDate(UltraVanilla.getConfig(player).getLong("last-promotion"), timeZone));
            String nextRole = plugin.getRoleShouldHave(player);
            if (nextRole != null) {
                sender.sendMessage(String.format(
                    "%sNext promotion: %s%s",
                    COLOR,
                    Palette.OBJECT, getDate(plugin.getNextRoleDate(player), timeZone)
                ));
                if (!plugin.hasRightRole(player)) {
                    sender.sendMessage(String.format(
                        "%sThey should be %s%s %sby now!",
                        COLOR,
                        plugin.getRoleColor(nextRole), nextRole,
                        COLOR
                    ));
                }
            }
        });
    }

    private void sendLocationInfo(CommandSender sender, OfflinePlayer player) {
        Position position = (Position) UltraVanilla.getConfig(player).get(Users.LOGOUT_LOCATION);
        if (position != null) {
            TextComponent textComponent = new TextComponent("Last logout location: ");
            textComponent.setColor(COLOR);
            TextComponent component = new TextComponent();
            component.setColor(ChatColor.WHITE);
            component.setText(position.toStringTrimmed());
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, position.getTpCommand()));
            textComponent.addExtra(component);
            sender.sendMessage(textComponent);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TimeZone timeZone = TimeZone.getDefault();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String zone = UltraVanilla.getConfig(player.getUniqueId()).getString("timezone");
            if (!(zone == null || zone.isEmpty())) {
                timeZone = TimeZone.getTimeZone(zone);
            }
        }
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
        if (!player.hasPlayedBefore()) {
            sender.sendMessage(Palette.NOUN + player.getName() + COLOR + " has not played here before.");
            return true;
        }
        switch (command.getName()) {
            case "seen":
                if (args.length != 2) {
                    return false;
                }
                if (args[1].equalsIgnoreCase("first")) {
                    sendFirstJoined(sender, player, timeZone);
                    return true;
                } else if (args[1].equalsIgnoreCase("last")) {
                    sendLastSeen(sender, player, timeZone);
                    return true;
                } else {
                    return false;
                }
            case "firstjoined":
                sendFirstJoined(sender, player, timeZone);
                return true;
            case "lastseen":
                sendLastSeen(sender, player, timeZone);
                return true;
        }
        return false;
    }

    private String getDate(long time, TimeZone timezone) {
        if (time == 0) {
            return "a long time ago";
        } else {
            Date date = new Date(time);
            DateFormat df = new SimpleDateFormat(plugin.getCommandString("seen.format.date-format"));
            df.setTimeZone(timezone);
            return df.format(date);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            Arrays.stream(plugin.getServer().getOfflinePlayers()).forEach(p -> suggestions.add(p.getName()));
        } else {
            if (command.getName().equals("seen")) {
                suggestions.add("first");
                suggestions.add("last");
            }
        }
        return getSuggestions(suggestions, args);
    }
}
