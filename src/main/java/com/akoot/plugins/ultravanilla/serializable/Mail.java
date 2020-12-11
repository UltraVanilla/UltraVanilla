package com.akoot.plugins.ultravanilla.serializable;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("Mail")
public class Mail implements ConfigurationSerializable {

    private String message;
    private UUID from;
    private long date;
    private boolean read;

    public static Mail deserialize(Map<String, Object> map) {
        Mail mail = new Mail();
        mail.setDate((long) map.get("date"));
        mail.setFrom(UUID.fromString(map.get("from").toString()));
        mail.setMessage(map.get("message").toString());
        mail.setRead((boolean) map.get("read"));
        return mail;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getFrom() {
        return from;
    }

    public void setFrom(UUID from) {
        this.from = from;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        map.put("from", from.toString());
        map.put("message", message);
        map.put("read", read);
        return map;
    }
}
