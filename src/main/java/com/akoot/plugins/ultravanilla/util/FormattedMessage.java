package com.akoot.plugins.ultravanilla.util;

public class FormattedMessage extends RawMessage {

    public FormattedMessage() {
        super();
    }

    public FormattedMessage(String message) {

//        Pattern pattern = Pattern.compile("");
//        Matcher matcher = pattern.matcher(message);
//        while(matcher.find()) {
//
//        }
    }


    public void addComponent(String color, String content) {
        components.add(new RawComponent(color, content));
    }

    public void addComponent(String content) {
        components.add(new RawComponent(content));
    }

    public void addComponent(RawComponent component) {
        components.add(component);
    }

    public void addComponent(String color, String content, String hoverText) {
        components.add(new RawComponent(color, content, hoverText));
    }
}
