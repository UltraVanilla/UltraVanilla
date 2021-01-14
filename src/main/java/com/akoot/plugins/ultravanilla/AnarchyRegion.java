package com.akoot.plugins.ultravanilla;

import java.util.ArrayList;
import java.util.List;

import com.akoot.plugins.ultravanilla.serializable.Position;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class AnarchyRegion {
    public static Location center = new Location(null, 0, 0, 0);

    public static double xMin = 0.0;
    public static double xMax = 0.0;
    public static double zMin = 0.0;
    public static double zMax = 0.0;


    public static boolean inside(Location location) {
        return location.getX() > xMin
            && location.getX() < xMax
            && location.getZ() > zMin
            && location.getZ() < zMax
            && location.getWorld().getUID() == center.getWorld().getUID();
        
    }

    public static void configure(UltraVanilla plugin)  {
        FileConfiguration config = plugin.getConfig();

        List<Double> xRange = config.getDoubleList("spectate-box.x");
        List<Double> zRange = config.getDoubleList("spectate-box.z");

        xMin = xRange.get(0);
        xMax = xRange.get(1);
        zMin = zRange.get(0);
        zMax = zRange.get(1);

        center.setWorld(Bukkit.getWorld(config.getString("spectate-box.world")));
        center.setX((xMax + xMin) / 2.0);
        center.setZ((zMax + zMin) / 2.0);
        center.setY(128.0);
    }
}
