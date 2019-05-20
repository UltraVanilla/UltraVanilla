package com.akoot.plugins.ultravanilla.stuff;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class Ticket {

    private UUID author;
    private int id;
    private String content;
    private String admin;
    private String reply;
    private Status status;
    private UltraVanilla plugin;

    public Ticket(UltraVanilla plugin) {
        this.plugin = plugin;
        this.id = plugin.getTickets().size() + 1;
    }

    public String getAdmin() {
        return admin;
    }

    public String getAuthor() {
        return plugin.getServer().getOfflinePlayer(author).getName();
    }

    public UUID getAuthorId() {
        return author;
    }

    public int getId() {
        return id;
    }

    public void load(Map<String, Object> map) {
        this.author = UUID.fromString(map.get("author").toString());
        this.content = map.get("content").toString();
        this.status = Status.valueOf(map.get("status").toString());
        this.reply = map.get("reply") != null ? map.get("reply").toString() : "";
        this.admin = map.get("admin") != null ? map.get("admin").toString() : "";
    }

    public String getPreview() {
        if (content.length() <= 24) {
            return content;
        } else {
            return content.substring(0, 24) + "...";
        }
    }

    public String getContent() {
        return content;
    }

    public String getReply() {
        return reply;
    }

    public Status getStatus() {
        return status;
    }

    public void remove() {
        plugin.getTickets().remove(this);
    }

    public void create(Player author) {
        this.author = author.getUniqueId();
        this.status = Status.OPEN;
        this.admin = "";
        this.reply = "";
        plugin.getTickets().add(this);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void reply(String admin, String response) {
        this.admin = admin;
        this.reply = response;
        this.status = Status.ANSWERED;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void reopen(String reply) {
        this.status = Status.OPEN;
        this.reply = null;
        this.content = reply;
    }

    public enum Status {
        CLOSED, OPEN, ANSWERED
    }

    public boolean isAuthor(CommandSender sender) {
        return sender instanceof Player && isAuthor((Player) sender);
    }

    public boolean isAuthor(Player player) {
        return author == player.getUniqueId();
    }

    public boolean hasResponse() {
        return reply != null && !reply.isEmpty();
    }

    public String toString() {
        return id + ":" + status + "\n" + author + ": " + content + "\n" + admin + ": " + reply;
    }
}
