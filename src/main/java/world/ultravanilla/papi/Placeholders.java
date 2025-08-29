package world.ultravanilla.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import world.ultravanilla.UltraVanilla;
import world.ultravanilla.reference.Users;

public class Placeholders extends PlaceholderExpansion {
    private final UltraVanilla plugin;

    public Placeholders(UltraVanilla plugin) {
        this.plugin = plugin;
    }

    @Override public String getIdentifier() { return "ultravanilla"; }
    @Override public String getAuthor() { return String.join(", ", plugin.getDescription().getAuthors()); }
    @Override public String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }
    @Override public boolean canRegister() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return "";
        return switch (params.toLowerCase()) {
            case "afk" -> Users.isAFK(player) ? " (AFK)" : "";
            case "afk_bool" -> Users.isAFK(player) ? "true" : "false";
            default -> null;
        };
    }
}
