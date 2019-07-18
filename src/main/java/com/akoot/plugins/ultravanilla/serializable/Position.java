package com.akoot.plugins.ultravanilla.serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Position implements ConfigurationSerializable {

    private String name;
    private UUID world;
    private double x, y, z;
    private float yaw, pitch;

    public Position(Location location) {
        this("location",
                location.getWorld().getUID(),
                location.getX(), location.getY(),
                location.getZ(), location.getPitch(),
                location.getYaw());
    }

    public Position(String name, UUID world, double x, double y, double z, float pitch, float yaw) {
        setName(name);
        setWorld(world);
        setX(x);
        setY(y);
        setZ(z);
        setPitch(pitch);
        setYaw(yaw);
    }

    public static Position deserialize(Map<String, Object> args) {
        return new Position((String) args.get("name"),
                UUID.fromString((String) args.get("world")),
                (double) args.get("x"),
                (double) args.get("y"),
                (double) args.get("z"),
                Float.valueOf(args.get("pitch") + "F"),
                Float.valueOf(args.get("yaw") + "F"));
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public UUID getWorld() {
        return world;
    }

    public void setWorld(UUID world) {
        this.world = world;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(getWorld()), getX(), getY(), getZ(), getYaw(), getPitch());
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();

        result.put("name", getName());
        result.put("world", getWorld().toString());
        result.put("x", getX());
        result.put("y", getY());
        result.put("z", getZ());
        result.put("pitch", getPitch());
        result.put("yaw", getYaw());

        return result;
    }

    @Override
    public String toString() {
        String worldName = Bukkit.getWorld(world).getName();
        worldName = worldName.equals("world") ? "" : " " + worldName + ",";
        return String.format("{%s:%s %s, %s, %s}", name, worldName, (int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
    }

    public String getTpCommand() {
        String worldName = "";
        switch (Bukkit.getWorld(world).getName().toLowerCase()) {
            case "world_nether":
                worldName = "the_nether";
                break;
            case "world":
                worldName = "overworld";
                break;
            case "world_the_end":
                worldName = "the_end";
                break;
        }
        return String.format("/execute in %s run tp %s %s %s", worldName, x, y, z);
    }

    public String toStringTrimmed() {
        return String.format("%s, %s, %s, %s", (int) Math.round(x), (int) Math.round(y), (int) Math.round(z), Bukkit.getWorld(world).getName());
    }
}
