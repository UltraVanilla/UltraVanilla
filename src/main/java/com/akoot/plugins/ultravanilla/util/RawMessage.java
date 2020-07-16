package com.akoot.plugins.ultravanilla.util;

import java.util.ArrayList;
import java.util.List;

public class RawMessage {

    protected final List<RawComponent> components;

    public RawMessage() {
        components = new ArrayList<>();
    }

    public String getJson() {
        StringBuilder json = new StringBuilder();

        if (!components.isEmpty()) {
            json = new StringBuilder("[");
            for (RawComponent component : components) {
                json.append("{\"text\":\"").append(component.getContent()).append("\"");

                if (component.isBold()) {
                    json.append(",\"bold\":true");
                }

                if (component.isItalic()) {
                    json.append(",\"italic\":true");
                }

                if (component.isStrikethrough()) {
                    json.append(",\"strikethrough\":true");
                }

                if (component.isUnderline()) {
                    json.append(",\"underline\":true");
                }

                if (component.isObfuscated()) {
                    json.append(",\"obfuscated\":true");
                }

                if (!(component.getColor() == null || component.getColor().isEmpty())) {
                    json.append(",\"color\":\"").append(component.getColor()).append("\"");
                }

                if (!(component.getHoverText() == null || component.getHoverText().isEmpty())) {
                    json.append(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"").append(component.getHoverText()).append("\"}");
                }

                if (!(component.getSuggestion() == null || component.getSuggestion().isEmpty())) {
                    json.append(",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"").append(component.getSuggestion()).append("\"}");
                } else if (!(component.getCommand() == null || component.getCommand().isEmpty())) {
                    json.append(",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"").append(component.getCommand()).append("\"}");
                } else if (!(component.getLink() == null || component.getLink().isEmpty())) {
                    json.append(",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"").append(component.getLink()).append("\"}");
                }

                json.append("},");
            }
            json = new StringBuilder(json.substring(0, json.length() - 1));
            json.append("]");
        }
        return json.toString();
    }

    public void addComponent(RawComponent component) {
        components.add(component);
    }

    public void addComponents(List<RawComponent> components) {
        this.components.addAll(components);
    }
}
