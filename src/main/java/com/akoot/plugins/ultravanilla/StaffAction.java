package com.akoot.plugins.ultravanilla;

import java.util.ArrayList;
import java.util.List;

public class StaffAction {

    private final long expires;
    private Type type;
    private List<String> sources;
    private List<String> targets;
    private String description;
    private List<String> links;
    private long created;

    public StaffAction(Type type, String description, long created) {
        this(type, description, created, -1);
    }

    public StaffAction(Type type, String description, String source, String target, long created) {
        this(type, description, source, target, created, -1);
    }

    public StaffAction(Type type, String description, long created, long expires) {
        this.type = type;
        this.description = description;
        this.sources = new ArrayList<>();
        this.targets = new ArrayList<>();
        this.links = new ArrayList<>();
        this.created = created;
        this.expires = expires;
    }

    public StaffAction(Type type, String description, String source, String target, long created, long expires) {
        this(type, description, created, expires);
        sources.add(source);
        targets.add(target);
    }

    public StaffAction(Type type, List<String> sources, List<String> targets, String description, List<String> links, long created, long expires) {
        this.type = type;
        this.sources = sources;
        this.targets = targets;
        this.description = description;
        this.links = links;
        this.created = created;
        this.expires = expires;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        PERMA_BAN, IP_BAN, BAN, TEMP_BAN, KICK, WARN, PARDON
    }
}
