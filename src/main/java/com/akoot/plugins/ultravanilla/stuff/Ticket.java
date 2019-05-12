package com.akoot.plugins.ultravanilla.stuff;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class Ticket {

    private UUID author;
    private int id;
    private String content;
    private String admin;
    private String response;
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
        this.response = map.get("response") != null ? map.get("response").toString() : null;
        this.admin = map.get("admin") != null ? map.get("admin").toString() : null;
    }

    public String getContentShortened() {
        if (content.length() <= 24) {
            return content;
        } else {
            return content.substring(0, 24) + "...";
        }
    }

    public String getContent() {
        return content;
    }

    public String getResponse() {
        return response;
    }

    public Status getStatus() {
        return status;
    }

    public void remove() {
        plugin.getTickets().remove(this);
    }

    public void close() {
        this.status = Status.CLOSED;
    }

    public void respond(String admin, String response) {
        this.admin = admin;
        this.status = Status.ANSWERED;
        this.response = response;
    }

    public void open(Player author, String content) {
        this.author = author.getUniqueId();
        this.status = Status.OPEN;
        this.content = content;
        plugin.getTickets().add(this);
    }

    public void reopen() {
        this.status = Status.OPEN;
    }

    public enum Status {
        CLOSED, OPEN, ANSWERED
    }
}
