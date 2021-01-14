package com.akoot.plugins.ultravanilla.reference;

import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class Users {

    public static final File DIR = new File("users");

    public static final String PING_ENABLED = "ping-enabled";
    public static final String IGNORED = "ignored-players";
    public static final String FIRST_LOGIN = "login.first";
    public static final String TP_DISABLED = "tp-disabled";
    public static final String LOGOUT_LOCATION = "logout.location";
    public static final String UNLOCKED_TITLES = "title.unlocked";
    public static final String DISPLAY_TITLE = "title.display";


    public static final Map<String, String> replies = new HashMap<>();
    public static final List<UUID> afk = new ArrayList<>();
    public static final HashSet<UUID> spectators = new HashSet<>();

    public static boolean isAFK(Player player) {
        return afk.contains(player.getUniqueId());
    }

    public static boolean isSpectator(Player player) {
        return spectators.contains(player.getUniqueId());
    }
}
