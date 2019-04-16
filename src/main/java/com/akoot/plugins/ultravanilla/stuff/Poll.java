package com.akoot.plugins.ultravanilla.stuff;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import com.akoot.plugins.ultravanilla.commands.VoteCommand;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.reference.Users;
import com.akoot.plugins.ultravanilla.util.RawComponent;
import com.akoot.plugins.ultravanilla.util.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class Poll extends BukkitRunnable {

    private String name;
    private String title;
    private Map<String, Integer> votes;
    private long time;
    private long start;

    public Poll(String name, String title, long time) {
        this.name = name;
        this.title = title;
        this.time = time;
        votes = new HashMap<>();
    }

    public static long getTime(String string) {
        long time;
        int number = Integer.parseInt(string.substring(0, string.length() - 1));
        char end = string.toCharArray()[string.length() - 1];
        switch (end) {
            case 's':
                time = number * 20L;
                break;
            case 'm':
                time = (number * 60) * 20L;
                break;
            case 'h':
                time = ((number * 60) * 60) * 20L;
                break;
            case 'd':
                time = (((number * 60) * 60) * 24) * 20L;
                break;
            default:
                time = (long) number;
        }
        return time;
    }

    public Map<String, Integer> getVotes() {
        return votes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void poll() {
        Bukkit.getServer().broadcastMessage(VoteCommand.COLOR + "Poll: " + Palette.NOUN + title);
        RawMessage message = new RawMessage();
        for (String key : votes.keySet()) {
            RawComponent text = new RawComponent();
            text.setContent(Palette.OBJECT + key + "\\n");
            text.setCommand("/vote " + name + " \\\"" + key + "\\\"");
            text.setHoverText(Palette.NOUN + "Click to vote \\\"" + key + "\\\"");
            message.addComponent(text);
        }
        RawComponent t = new RawComponent();
        t.setContent(VoteCommand.COLOR + "Time: " + Palette.NUMBER + getHumanTime(time));
        message.addComponent(t);
        Ultravanilla.tellRaw(message);
    }

    public void show(Player player) {
        player.sendMessage(VoteCommand.COLOR + "Poll Info: " + Palette.NOUN + title);
        RawMessage message = new RawMessage();
        for (String key : votes.keySet()) {
            RawComponent text = new RawComponent();
            text.setContent(Palette.OBJECT + key + "\\n");
            text.setCommand("/vote " + name + " \\\"" + key + "\\\"");
            text.setHoverText(Palette.NOUN + "" + votes.get(key) + VoteCommand.COLOR + " votes");
            message.addComponent(text);
        }
        Ultravanilla.tellRaw(message, player);
        player.sendMessage(VoteCommand.COLOR + "" + "Time left: " + Palette.NUMBER + getHumanTime(getTimeRemaining()));
    }

    public void init() {
        poll();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Ultravanilla.getInstance(), this, time);
        start = System.currentTimeMillis();
    }

    public void vote(String key) {
        Integer value = votes.get(key);
        if (value != null) {
            votes.put(key, value + 1);
        }
    }

    @Override
    public void run() {
        Bukkit.getServer().broadcastMessage(VoteCommand.COLOR + "Poll results: " + Palette.NOUN + title + Palette.NUMBER + " (" + getTotalVotes() + " votes)");
        RawMessage message = new RawMessage();
        for (String key : votes.keySet()) {
            int value = votes.get(key);
            String percent = (int) Math.round((getTotalVotes() > 0 ? ((double) (value) / (double) (getTotalVotes())) * 100.0 : 0.0)) + "%";
            RawComponent component = new RawComponent();
            component.setContent(Palette.OBJECT + key + ": " + Palette.NUMBER + percent + "\\n");
            component.setHoverText(Palette.NOUN + "" + value + VoteCommand.COLOR + " votes");
            message.addComponent(component);
        }
        Ultravanilla.tellRaw(message);
        Ultravanilla.getInstance().getPolls().remove(this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Ultravanilla.remove(player.getUniqueId(), Users.VOTE_LIST, name);
        }
    }

    private int getTotalVotes() {
        int total = 0;
        for (String key : votes.keySet()) {
            total += votes.get(key);
        }
        return total;
    }

    private long getTimeRemaining() {
        return time - (((System.currentTimeMillis() - start) / 1000L) * 20L);
    }

    public String getHumanTime(long t) {
        String humanTime;

        int seconds = (int) (t / 20L);
        if (seconds > 60) {
            int minutes = seconds / 60;
            seconds = seconds % 60;
            humanTime = minutes + "m";
            if (seconds > 0) {
                humanTime += seconds + "s";
            }
        } else {
            humanTime = seconds + "s";
        }

        return humanTime;
    }
}
