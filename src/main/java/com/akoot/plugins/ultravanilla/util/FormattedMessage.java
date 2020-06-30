package com.akoot.plugins.ultravanilla.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormattedMessage {

    private final List<FormattedComponent> components;

    public FormattedMessage() {
        this.components = new ArrayList<>();
    }

    public FormattedMessage(String message) {

        this.components = new ArrayList<>();
        Pattern pattern = Pattern.compile("");
        Matcher matcher = pattern.matcher(message);
//        while(matcher.find()) {
//
//        }
    }

    public String getJson() {

        StringBuilder json = new StringBuilder();

        if (!components.isEmpty()) {

            json = new StringBuilder("[");

            for (FormattedComponent component : components) {

                StringBuilder item = new StringBuilder();

                item.append("{\"text\":\"").append(component.getContent()).append("\"");
                boolean plain = true;

                if (component.isBold()) {
                    item.append(",\"bold\":true");
                    plain = false;
                }

                if (component.isItalic()) {
                    item.append(",\"italic\":true");
                    plain = false;
                }

                if (component.isStrikethrough()) {
                    item.append(",\"strikethrough\":true");
                    plain = false;
                }

                if (component.isUnderline()) {
                    item.append(",\"underline\":true");
                    plain = false;
                }

                if (component.isObfuscated()) {
                    item.append(",\"obfuscated\":true");
                    plain = false;
                }

                if (!(component.getColor() == null || component.getColor().isEmpty())) {
                    item.append(",\"color\":\"").append(component.getColor()).append("\"");
                    plain = false;
                }

                if (plain) {
                    item = new StringBuilder("\"").append(component.getContent()).append("\",");
                } else {
                    item.append("},");
                }
                json.append(item);
            }

            json = new StringBuilder(json.substring(0, json.length() - 1));
            json.append("]");
        }

        return json.toString();
    }

    public void addComponent(String color, String content) {
        components.add(new FormattedComponent(color, content));
    }

    public void addComponent(String content) {
        components.add(new FormattedComponent(content));
    }

    public void addComponent(FormattedComponent component) {
        components.add(component);
    }
}
