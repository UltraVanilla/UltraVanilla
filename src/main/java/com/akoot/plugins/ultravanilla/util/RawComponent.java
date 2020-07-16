package com.akoot.plugins.ultravanilla.util;

import java.util.ArrayList;
import java.util.List;

public class RawComponent {

    private String content;
    private String link;
    private String suggestion;
    private String command;
    private String hoverText;
    private String color;
    private boolean bold, italic, strikethrough, obfuscated, underline;

    public RawComponent() {
    }

    public RawComponent(String color, String content) {
        this.color = color;
        this.content = content;
    }

    public RawComponent(String color, String content, String hoverText) {
        this.color = color;
        this.content = content;
        this.hoverText = hoverText;
    }

    public RawComponent(String content) {
        this.content = content;
    }

    public static List<RawComponent> parseColors(String colors) {
        List<RawComponent> rawComponents = new ArrayList<>();

        String[] groups = colors.split(",");
        for (String group : groups) {
            RawComponent component = new RawComponent();
            String color = group.substring(0, group.indexOf(":"));
            if (color.contains("+")) {
                String modifiers = color.substring(colors.indexOf("+") + 1);
                for (char c : modifiers.toCharArray()) {
                    switch (c) {
                        case 'b':
                            component.bold = true;
                            break;
                        case 'i':
                            component.italic = true;
                            break;
                        case 'u':
                            component.underline = true;
                            break;
                        case 's':
                            component.strikethrough = true;
                            break;
                        case 'o':
                            component.obfuscated = true;
                            break;
                    }
                }
                color = color.substring(0, color.indexOf("+"));
            }
            String content = group.substring(group.indexOf(":") + 1);
            component.color = color;
            component.content = content;
            rawComponents.add(component);
        }

        return rawComponents;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isStrikethrough() {
        return strikethrough;
    }

    public void setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    public boolean isObfuscated() {
        return obfuscated;
    }

    public void setObfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getHoverText() {
        return hoverText;
    }

    public void setHoverText(String hoverText) {
        this.hoverText = hoverText;
    }
}
