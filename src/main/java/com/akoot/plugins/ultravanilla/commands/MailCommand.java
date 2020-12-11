package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.serializable.Mail;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class MailCommand extends UltraCommand implements CommandExecutor, TabExecutor, Listener {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public MailCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /**
         *       /mail send <player> <message>
         *       /mail read
         *       /mail clear
         *       /mail open <id>
         *       /mail delete <id>
         *       /mail reply <id> <message>
         */
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("read")) {

            } else if (args[0].equalsIgnoreCase("clear")) {

            } else {

            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("open")) {
                int id = Integer.parseInt(args[1]);
            } else if (args[0].equalsIgnoreCase("delete")) {
                int id = Integer.parseInt(args[1]);
            } else {

            }
        } else if (args.length >= 3) {
            String message = getArg(args, 2);
            if (args[0].equalsIgnoreCase("send")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[1]);
                    YamlConfiguration config = UltraVanilla.getConfig(target.getUniqueId());
                    if (config != null) {
                        List<Mail> mailList = (List<Mail>) config.getList("mail");
                        if (mailList == null) {
                            mailList = new ArrayList<>();
                        }
                        Mail mail = new Mail();
                        mail.setMessage(message);
                        mail.setFrom(player.getUniqueId());
                        mail.setDate(System.currentTimeMillis());
                        mailList.add(mail);
                        config.set("mail", mail);
                    } else {

                    }
                } else {

                }
            } else if (args[0].equalsIgnoreCase("reply")) {
                int id = Integer.parseInt(args[1]);

            } else {

            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }
}
