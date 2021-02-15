package world.ultravanilla.serializable;

import world.ultravanilla.UltraVanilla;
import world.ultravanilla.reference.Users;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Title implements ConfigurationSerializable {

    private String id, name;
    private Rarity rarity;

    public Title(String id, String name, Rarity rarity) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
    }

    public static Title deserialize(Map<String, Object> args) {
        return new Title(args.get("id").toString(), args.get("name").toString(), Rarity.valueOf(args.get("rarity").toString()));
    }

    public static boolean titleExists(String id) {
        for (Title title : getTitles()) {
            if (title.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasTitleUnlocked(OfflinePlayer player, String id) {
        return false;
    }

    public static List<Title> getTitles() {
        return (List<Title>) UltraVanilla.getInstance().getConfig().getList("titles");
    }

    public static void setDisplayTitle(OfflinePlayer player, String id) {
        UltraVanilla.getPlayerConfig(player).set(Users.DISPLAY_TITLE, id);
    }

    public static List<Title> getUnlockedTitles(OfflinePlayer player) {
        return (List<Title>) UltraVanilla.getPlayerConfig(player).getList(Users.UNLOCKED_TITLES);
    }

    public static List<String> getUnlockedTitleNames(OfflinePlayer player) {
        List<String> list = new ArrayList<>();
        for (Title title : getUnlockedTitles(player)) {
            list.add(title.getName());
        }
        return list;
    }

    public static void addTitle(Title title) {
        getTitles().add(title);
        UltraVanilla.getInstance().saveConfig();
    }

    public static void removeTitle(String id) {
        for (Title title : getTitles()) {
            if (title.getId().equals(id)) {
                getTitles().remove(title);
                UltraVanilla.getInstance().saveConfig();
                return;
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();

        result.put("id", id);
        result.put("name", name);
        result.put("rarity", rarity.name());

        return result;
    }

    public static enum Rarity {
        COMMON, RARE, EPIC, LEGENDARY
    }
}
