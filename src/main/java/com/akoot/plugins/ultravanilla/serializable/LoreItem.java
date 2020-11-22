package com.akoot.plugins.ultravanilla.serializable;

import com.akoot.plugins.ultravanilla.reference.Palette;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("LoreItem")
public class LoreItem implements ConfigurationSerializable {

    private String name;
    private List<String> lore;
    private Position position;

    public LoreItem(String name, List<String> lore, Position position) {
        this.name = name;
        this.lore = lore;
        this.position = position;

        if (this.lore == null) this.lore = new ArrayList<>();
        if (this.name == null) this.name = "";
    }

    public static LoreItem deserialize(Map<String, Object> map) {
        return new LoreItem(
                Palette.translate(map.get("name").toString()),
                Palette.translate((List<String>) map.get("lore")),
                Position.of(map.get("pos") + "")
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("pos", position.toStringLite());
        map.put("lore", Palette.untranslate(lore));
        map.put("name", Palette.untranslate(name));
        return map;
    }

    @Override
    public String toString() {
        return String.format("loc: %s\nname: %s\nlore: [%s]", position.toStringTrimmed(), name, String.join(", ", lore));
    }
}
