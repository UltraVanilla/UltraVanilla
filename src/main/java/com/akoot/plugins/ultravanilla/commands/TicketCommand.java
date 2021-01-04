package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.stuff.Ticket;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TicketCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GRAY;
    private static final String STAFF = "ultravanilla.command.ticket.staff";

    public TicketCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // One argument
        if (args.length == 1) {

            // ticket list
            if (args[0].equalsIgnoreCase("list")) {
                // Title
                String title = format(command, "format.list.title");
                sender.sendMessage(plugin.getTitle(title, color));

                // Items: tickets
                for (Ticket ticket : plugin.getTickets()) {
                    // Generate format
                    String[] format = {
                            "{id}", ticket.getId() + "",
                            "{preview}", ticket.getPreview(),
                            "{status}", ticket.getStatus().toString(),
                            "{author}", ticket.getAuthor()
                    };

                    // If the sender is a staff member
                    if (sender.hasPermission(STAFF)) {
                        sender.sendMessage(format(command, "format.list.item.staff", format));
                    }
                    // If the sender is the author
                    else if (ticket.isAuthor(sender)) {
                        sender.sendMessage(format(command, "format.list.item.self", format));
                    }
                    // If the sender is neither a staff member nor the author
                    else {
                        sender.sendMessage(format(command, "format.list.item.player", format));
                    }
                }
            } else {
                return false;
            }
        }
        // More than one argument
        else if (args.length >= 2) {
            // ticket create <message>
            if (args[0].equalsIgnoreCase("create")) {
                // If the sender is a player
                if (sender instanceof Player) {
                    // Set player variable
                    Player player = (Player) sender;

                    // Generate a new ticket and open it
                    Ticket ticket = new Ticket(plugin);
                    ticket.setContent(getArg(args, 2));
                    ticket.create(player);

                    // Send create message to sender
                    sender.sendMessage(format(command, "message.create.player", "{id}", ticket.getId() + ""));

                    // Send create message to staff members
                    plugin.sendMessageToStaff(format(command, "message.create.staff", "{id}", ticket.getId() + "", "{player}", ticket.getAuthor()), STAFF);

                }
                // If the sender is not a player
                else {
                    sender.sendMessage(plugin.getString("player-only", "{action}", "create tickets"));
                }
            }
            // ticket read|close|delete|answer <id> [message]
            else {
                // Get ID from the 2nd argument
                int id = getInt(sender, args[1]);

                // Get the ticket from memory using the ID as reference
                Ticket ticket = plugin.getTicket(id);

                // If ticket with specified ID exists
                if (ticket != null) {
                    // Generate format
                    String[] format = {
                        "{author's}", possessive(ticket.getAuthor()),
                        "{author}", ticket.getAuthor(),
                        "{id}", ticket.getId() + "",
                        "{admin}", ticket.getAdmin(),
                        "{response}", ticket.getReply(),
                        "{content}", ticket.getContent(),
                        "{preview}", ticket.getPreview(),
                        "{status}", ticket.getStatus().toString()
                    };

                    // ticket read|delete|open|close <id>
                    if (args.length == 2) {
                        // ticket read <id>
                        if (args[0].equalsIgnoreCase("read")) {
                            // If the sender is a player
                            if (sender instanceof Player) {
                                // Set player variable
                                Player player = (Player) sender;

                                // If the sender is the author
                                if (ticket.isAuthor(player)) {
                                    sender.sendMessage(format(command, "format.read.content.self", format));
                                    if (ticket.hasResponse()) {
                                        sender.sendMessage(format(command, "format.read.reply.self", format));
                                    }
                                }
                                // If the sender is a staff member
                                else if (sender.hasPermission(STAFF)) {
                                    sender.sendMessage(format(command, "format.read.content.staff", format));
                                    if (ticket.hasResponse()) {
                                        sender.sendMessage(format(command, "format.read.reply.staff", format));
                                    }
                                }
                                // If the sender is not the author or a staff member
                                else {
                                    sender.sendMessage(format(command, "format.read.content.player", format));
                                    if (ticket.hasResponse()) {
                                        sender.sendMessage(format(command, "format.read.reply.player", format));
                                    }
                                }
                            } else {
                                sender.sendMessage(format(command, "format.read.content.staff", format));
                                if (ticket.hasResponse()) {
                                    sender.sendMessage(format(command, "format.read.reply.staff", format));
                                }
                            }
                        }
                        // ticket delete <id>
                        else if (args[0].equalsIgnoreCase("delete")) {
                            if (ticket.isAuthor(sender) || sender.hasPermission(STAFF)) {
                                // Remove the ticket from memory
                                plugin.getTickets().remove(ticket);

                                // Send delete message to sender
                                sender.sendMessage(format(command, "message.deleted", format));

                                // Send delete message to staff members
                                plugin.sendMessageToStaff(format(command, "message.delete.staff", format), STAFF);
                            } else {
                                sender.sendMessage(plugin.getString("no-permission", "{action}", "delete this ticket"));
                            }
                        }
                        // ticket close <id>
                        else if (args[0].equalsIgnoreCase("close")) {
                            // If sender is the author or a staff member
                            if (ticket.isAuthor(sender) || sender.hasPermission(STAFF)) {
                                // Mark the ticket as closed
                                ticket.setStatus(Ticket.Status.CLOSED);

                                // Send close message to sender
                                sender.sendMessage(format(command, "message.close.player", "{id}", ticket.getId() + ""));

                                // Send close message to staff members
                                plugin.sendMessageToStaff(format(command, "message.close.staff", format), STAFF);
                            }
                            // If sender is not the author nor a staff member
                            else {
                                sender.sendMessage(plugin.getString("no-permission", "{action}", "close this ticket"));
                            }
                        }
                    } else {
                        // ticket <id> reply [message]
                        if (args[0].equalsIgnoreCase("reply")) {
                            // If sender is a staff member
                            if (sender.hasPermission(STAFF)) {
                                // Set the ticket response
                                ticket.reply(sender.getName(), getArg(args, 3));

                                // Send reply message to staff member
                                sender.sendMessage(format(command, "message.reply.staff", format));

                                // If the author is online, send reply message to author
                                Player author = plugin.getServer().getPlayer(ticket.getAuthorId());
                                if (author != null) {
                                    author.sendMessage(format(command, "message.reply.player", format));
                                }
                            }
                            // If sender is the author
                            else if (ticket.isAuthor(sender)) {
                                // If the ticket is already replied to by a staff member
                                if (ticket.hasResponse()) {
                                    // Reopen the ticket with a reply
                                    ticket.reopen(getArg(args, 3));

                                    // Send reply message to sender
                                    sender.sendMessage(format(command, "message.reply.author", format));

                                    // Send reply message to staff members
                                    plugin.sendMessageToStaff(format(command, "message.reply.staff", format), STAFF);
                                }
                                // If the ticket has not been replied to by a staff member
                                else {
                                    sender.sendMessage(format(command, "error.no-reply"));
                                }
                            }
                            // If sender is not the author or a staff member
                            else {
                                sender.sendMessage(plugin.getString("no-permission", "{action}", "reply to this ticket"));
                            }
                        }
                    }
                }
                // If ticket with specified ID does not exist
                else {
                    sender.sendMessage(format(command, "error.invalid-id", "{id}", args[1]));
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
            suggestions.add("delete");
            if (sender.hasPermission(STAFF)) {
                suggestions.add("reply");
            }

        }
        if (args.length == 2) {
            if (args[0].matches("read|close|delete")) {
                for (Ticket ticket : plugin.getTickets()) {
                    if (sender.hasPermission(STAFF) || sender instanceof Player && ((Player) sender).getUniqueId().equals(ticket.getAuthorId())) {
                        suggestions.add(ticket.getId() + "");
                    }
                }
            }
        }
        return suggestions;
    }
}
