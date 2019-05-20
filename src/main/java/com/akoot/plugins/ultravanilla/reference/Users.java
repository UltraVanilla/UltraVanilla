package com.akoot.plugins.ultravanilla.reference;

import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class Users {

    public static final File DIR = new File("users");

    public static final String NICKNAME = "nickname";
    public static final String PING_ENABLED = "ping-enabled";
    public static final String VOTE_LIST = "vote-list";
    public static final String IGNORED = "ignored-players";
    public static final String HOMES = "homes";
    public static final String LAST_LOGIN = "login.last";
    public static final String FIRST_LOGIN = "login.first";

    public static final Map<String, String> REPLIES = new HashMap<>();
    public static final List<UUID> AFK = new ArrayList<>();

    public static boolean isAFK(Player player) {
        return AFK.contains(player.getUniqueId());
    }
}
