package com.akoot.plugins.ultravanilla.stuff;

import com.akoot.plugins.ultravanilla.reference.Palette;
import net.md_5.bungee.api.ChatColor;

public class Channel {

    private String sender;
    private String recipient;
    private ChatColor color;

    public Channel(String sender, String recipient) {
        this.sender = sender;
        this.recipient = recipient;
        this.color = Palette.random();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return String.format("{sender: %s,recipient: %s, color: %s}", sender, recipient, color.getColor().toString());
    }
}
