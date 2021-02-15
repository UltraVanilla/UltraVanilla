package world.ultravanilla.serializable;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class Powertool implements ConfigurationSerializable {

    private Material material;
    private String content;

    public Powertool() {

    }

    //TODO: serialize properly
    public Powertool(Material material, String content) {
        this.material = material;
        this.content = content;
    }

    public static Powertool deserialize(Map<String, Object> args) {
        return new Powertool(Material.valueOf((String) args.get("material")), (String) args.get("content"));
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("material", getMaterial().name());
        result.put("content", getContent());

        return result;
    }
}
