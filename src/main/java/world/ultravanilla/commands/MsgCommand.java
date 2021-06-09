package world.ultravanilla.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import world.ultravanilla.UltraVanilla;
import world.ultravanilla.reference.Palette;
import world.ultravanilla.reference.Users;
import world.ultravanilla.stuff.ChannelHandler;

import java.util.ArrayList;
import java.util.List;

public class MsgCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GRAY;

    public MsgCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    public static void msg(CommandSender from, CommandSender to, String message) {

        ChatColor fromChannelColor = Palette.random();
        ChatColor toChannelColor = Palette.random();
        ChatColor channelColor;

        if (to instanceof Player) {
            Player player = (Player) to;
            if (!UltraVanilla.getPlayerConfig(player).getBoolean("channels.direct-disabled", false)) {
                String path = "channels.direct." + from.getName();
                String color = UltraVanilla.getPlayerConfig(player).getString(path);
                if (color == null) {
                    UltraVanilla.set(player, path, Palette.getHex(toChannelColor));
                } else {
                    toChannelColor = ChatColor.of(color);
                }

                if (Users.isAFK((Player) to)) {
                    from.sendMessage(Palette.NOUN + to.getName() + COLOR + " is currently AFK");
                }
            } else {
                toChannelColor = ChatColor.GRAY;
            }
        }

        if (from instanceof Player) {
            Player player = (Player) from;
            if (!UltraVanilla.getPlayerConfig(player).getBoolean("channels.direct-disabled", false)) {
                String path = "channels.direct." + to.getName();
                String color = UltraVanilla.getPlayerConfig(player).getString(path);
                if (color == null) {
                    UltraVanilla.set(player, path, Palette.getHex(fromChannelColor));
                } else {
                    fromChannelColor = ChatColor.of(color);
                }
                channelColor = fromChannelColor;
            } else {
                fromChannelColor = ChatColor.GRAY;
                channelColor = ChatColor.GRAY;
            }
        } else {
            channelColor = toChannelColor;
        }

        UltraVanilla plugin = UltraVanilla.getInstance();
        String fromFormat = plugin.getString("command.message.format.from");
        String toFormat = plugin.getString("command.message.format.to");
        String spyFormat = plugin.getString("command.message.format.spy");

        from.sendMessage(Palette.translate(toFormat
                .replace("&:", fromChannelColor + "")
                .replace("{message}", message)
                .replace("{recipient}", to.getName())
        ));

        to.sendMessage(Palette.translate(fromFormat
                .replace("&:", toChannelColor + "")
                .replace("{message}", message)
                .replace("{player}", from.getName())
        ));

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("ultravanilla.chat.spy")
                    && !(player.getName().equals(from.getName())
                    || player.getName().equals(to.getName()))) {
                player.sendMessage(Palette.translate(spyFormat
                        .replace("&:", channelColor + "")
                        .replace("{message}", message)
                        .replace("{player}", from.getName())
                        .replace("{recipient}", to.getName())
                ));
            }
        }

        ChannelHandler.getChannels().put(from.getName(), to.getName());
        ChannelHandler.getChannels().put(to.getName(), from.getName());


    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            CommandSender to;
            if (args[0].equalsIgnoreCase("console")) {
                to = Bukkit.getConsoleSender();
            } else {
                to = plugin.getServer().getPlayer(args[0]);
            }
            if (to != null) {
                msg(sender, to, getArg(args, 2));
            } else {
                sender.sendMessage(format(command, "player-offline", "{player}", args[0]));
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        } else {
            return new ArrayList<>();
        }
    }
}
