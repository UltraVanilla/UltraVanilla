package world.ultravanilla

import org.bukkit.{Bukkit, Location}

object AnarchyRegion {
    var center = new Location(null, 0, 0, 0)
    var xMin = 0.0
    var xMax = 0.0
    var zMin = 0.0
    var zMax = 0.0

    var allowTeleport = true

    def inside(location: Location): Boolean =
        location.getX > xMin && location.getX < xMax && location.getZ > zMin && location.getZ < zMax &&
            (location.getWorld.getUID eq center.getWorld.getUID)

    def configure(plugin: UltraVanilla): Unit = {
        val config = plugin.getConfig
        val xRange = config.getDoubleList("spectate-box.x")
        val zRange = config.getDoubleList("spectate-box.z")

        xMin = xRange.get(0)
        xMax = xRange.get(1)
        zMin = zRange.get(0)
        zMax = zRange.get(1)
        center.setWorld(Bukkit.getWorld(config.getString("spectate-box.world")))
        center.setX((xMax + xMin) / 2.0)
        center.setZ((zMax + zMin) / 2.0)
        center.setY(128.0)

        allowTeleport = config.getBoolean("spectate-box.allow-teleport")
    }
}
