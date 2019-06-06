package com.akoot.plugins.ultravanilla.stuff;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {

    private UUID owner;
    private List<UUID> members;
    private String id;

    public Channel(Player owner) {
        this.owner = owner.getUniqueId();
        this.id = UUID.randomUUID().toString().substring(0, 4);
    }

    public Channel(String id) {
        UltraVanilla.getInstance().getStorage().getMapList("");
    }

    public String getId() {
        return id;
    }

    public boolean isMember(Player player) {
        for (UUID uuid : members) {
            if (player.getUniqueId().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean addMember(Player player) {
        if (!isMember(player)) {
            members.add(player.getUniqueId());
            return true;
        }
        return false;
    }

    public boolean kickMember(Player player) {
        for (UUID uuid : members) {
            if (player.getUniqueId().equals(uuid)) {
                members.remove(uuid);
                return true;
            }
        }
        return false;
    }

    public List<Player> getOnlineMembers() {
        List<Player> players = new ArrayList<>();
        for (UUID uuid : members) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    public List<OfflinePlayer> getOfflineMembers() {
        List<OfflinePlayer> offlinePlayers = new ArrayList<>();
        for (UUID uuid : members) {
            OfflinePlayer offlinePlayer = Bukkit.getPlayer(uuid);
            if (offlinePlayer != null && !offlinePlayer.isOnline()) {
                offlinePlayers.add(offlinePlayer);
            }
        }
        return offlinePlayers;
    }

}
