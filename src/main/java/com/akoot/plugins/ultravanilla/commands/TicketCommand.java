package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.stuff.Ticket;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TicketCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GRAY;

    public TicketCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                for (Ticket ticket : plugin.getTickets()) {
                    boolean canSee = sender.hasPermission("ultravanilla.command.ticket.admin") || (sender instanceof Player && ((Player) sender).getUniqueId().equals(ticket.getAuthorId()));
                    sender.sendMessage(format("#%s %s %s(%s)",
                            number(ticket.getId() + ""),
                            quote(ticket.getContentShortened()),
                            canSee ? noun("-" + ticket.getAuthor() + " ") : "",
                            object(ticket.getStatus() + "")
                    ));
                }
                return true;
            }
        } else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("create")) {
                if (sender instanceof Player) {
                    Ticket ticket = new Ticket(plugin);
                    ticket.open(((Player) sender), getArg(args, 2));
                    sender.sendMessage(format("Created ticket with ID #%s!", number(ticket.getId() + "")));
                } else {
                    sender.sendMessage(playerOnly());
                }
            } else {
                int id = getInt(sender, args[1]);
                Ticket ticket = plugin.getTicket(id);
                if (ticket != null) {
                    boolean owner = sender.hasPermission("ultravanilla.command.ticket.admin") || sender instanceof Player && ((Player) sender).getUniqueId().equals(ticket.getAuthorId());
                    if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("read")) {
                            String author = ticket.getAuthor();
                            String content = ticket.getContent();
                            if (sender instanceof Player) {
                                if (((Player) sender).getUniqueId().equals(ticket.getAuthorId())) {
                                    if (ticket.getResponse() != null) {
                                        author = ticket.getAdmin();
                                        content = ticket.getResponse();
                                    } else {
                                        sender.sendMessage(format(Palette.VERB + "No response yet..."));
                                    }
                                } else {
                                    author = "Anonymous";
                                }
                            }
                            sender.sendMessage(format("%s says: %s", noun(author), quote(content)));
                        } else if (args[0].equalsIgnoreCase("delete")) {
                            if (owner) {
                                sender.sendMessage(format("Deleted ticket #%s", number(ticket.getId() + "")));
                                plugin.getTickets().remove(ticket);
                            } else {
                                sender.sendMessage("You do not have access to this ticket!");
                            }
                        } else if (args[0].equalsIgnoreCase("open")) {
                            sender.sendMessage(format("Ticket #%s has been set to %s", number(ticket.getId() + ""), object(Ticket.Status.OPEN.toString())));
                            ticket.reopen();
                        } else if (args[0].equalsIgnoreCase("close")) {
                            if (owner) {
                                ticket.close();
                                sender.sendMessage(format("Ticket #%s has been set to %s", number(ticket.getId() + ""), object(Ticket.Status.CLOSED.toString())));
                            }
                        }
                    } else {
                        if (args[0].equalsIgnoreCase("respond")) {
                            if (sender.hasPermission("ultravanilla.command.ticket.admin")) {
                                ticket.respond(sender.getName(), getArg(args, 3));
                                sender.sendMessage(format("You answered %s ticket!", posessiveNoun(ticket.getAuthor())));
                            } else {
                                sender.sendMessage(noPermission("respond to tickets"));
                            }
                        }
                    }
                } else {
                    sender.sendMessage(format(wrong("Ticket %s not found!"), quote(number(args[1]))));
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("create");
            suggestions.add("list");
            suggestions.add("read");
            suggestions.add("close");
            suggestions.add("open");
            suggestions.add("delete");
            if (sender.hasPermission("ultravanilla.command.ticket.admin")) {
                suggestions.add("respond");
            }

        }
        if (args.length == 2) {
            if (args[0].matches("read|close|open|delete")) {
                for (Ticket ticket : plugin.getTickets()) {
                    if (sender.hasPermission("ultravanilla.command.ticket.admin") || sender instanceof Player && ((Player) sender).getUniqueId().equals(ticket.getAuthorId())) {
                        suggestions.add(ticket.getId() + "");
                    }
                }
            }
        }
        return suggestions;
    }
}
