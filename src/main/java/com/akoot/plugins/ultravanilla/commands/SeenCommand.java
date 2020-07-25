package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SeenCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.YELLOW;

    public SeenCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TimeZone timezone = TimeZone.getDefault();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String zone = UltraVanilla.getConfig(player.getUniqueId()).getString("timezone", "");
            if (!(zone == null || zone.isEmpty())) {
                timezone = TimeZone.getTimeZone(zone);
            }
        }
        if (args.length > 0) {
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
            if (player.hasPlayedBefore()) {
                if (args.length == 1) {
                    long lastJoin = player.getLastSeen();
                    sender.sendMessage(format(command, "format.seen.last", "{player}", player.getName(), "{date}", getDate(lastJoin, timezone)));
                } else if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("first")) {
                        long firstJoin = UltraVanilla.getConfig(player.getUniqueId()).getLong(Users.FIRST_LOGIN);
                        sender.sendMessage(format(command, "format.seen.first", "{player}", player.getName(), "{date}", getDate(firstJoin, timezone)));
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
                if (sender.hasPermission("ultravanilla.permission.admin")) {
                    Position position = (Position) UltraVanilla.getConfig(player).get(Users.LOGOUT_LOCATION);
                    String locationText = color + "Last Location: " + ChatColor.RESET + position.toStringTrimmed();
                    if (sender instanceof Player) {
                        TextComponent textComponent = new TextComponent();
                        TextComponent component = new TextComponent();
                        component.setText(locationText);
                        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, position.getTpCommand()));
                        textComponent.addExtra(component);
                        sender.sendMessage(textComponent);
                    } else {
                        sender.sendMessage(locationText);
                    }
                }
            } else {
                sender.sendMessage(plugin.getString("player-unknown", "{player}", args[0]));
            }
        } else {
            return false;
        }
        return true;
    }

    private String getDate(long time, TimeZone timezone) {
        Date date = new Date(time);
        DateFormat df = new SimpleDateFormat(plugin.getCommandString("seen.format.date-format"));
        df.setTimeZone(timezone);
        return df.format(date);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            for (OfflinePlayer player : plugin.getServer().getOfflinePlayers()) {
                String name = player.getName();
                if (name != null && (args[0].length() < 1 || name.toLowerCase().startsWith(args[0].toLowerCase()))) {
                    suggestions.add(name);
                }
            }
        } else if (args.length == 2) {
            suggestions.add("first");
        }
        return suggestions;
    }
}
