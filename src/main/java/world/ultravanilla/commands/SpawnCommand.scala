package world.ultravanilla.commands

import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import world.ultravanilla.AnarchyRegion
import world.ultravanilla.UltraVanilla
import world.ultravanilla.serializable.Position

object SpawnCommand {
    val COLOR = ChatColor.GREEN
}

class SpawnCommand(val instance: UltraVanilla) extends UltraCommand(instance) with CommandExecutor {
    this.color = SpawnCommand.COLOR

    override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
        if (args.length == 0) if (sender.isInstanceOf[Player]) {
            val player = sender.asInstanceOf[Player]
            if (!AnarchyRegion.allowTeleport && AnarchyRegion.inside(player.getLocation)) return true
            val spawn = plugin.getConfig.get("spawn").asInstanceOf[Position]
            if (spawn != null) player.teleport(spawn.getLocation)
            else sender.sendMessage(format("spawn", "error.not-set"))
        } else sender.sendMessage(plugin.getString("player-only", "{action}", "set or go to the spawn"))
        else if (args.length == 1)
            if (args(0).equalsIgnoreCase("set")) if (sender.isInstanceOf[Player]) {
                val player = sender.asInstanceOf[Player]
                if (player.hasPermission("ultravanilla.command.spawn.set")) {
                    player.sendMessage(format("spawn", "message.set"))
                    plugin.getConfig.set("spawn", new Position(player.getLocation))
                    plugin.saveConfig()
                } else sender.sendMessage(plugin.getString("no-permission", "{action}", "set spawn"))
            } else sender.sendMessage(plugin.getString("player-only", "{action}", "set or go to the spawn"))
            else {
                val spawn = plugin.getConfig.get("spawn").asInstanceOf[Position]
                val player = plugin.getServer.getPlayer(args(0))
                if (player == null) {
                    sender.sendMessage(plugin.getString("player-offline", "{player}", args(0)))
                    return true
                }
                if (spawn != null)
                    if (sender.hasPermission("ultravanilla.command.spawn.player")) player.teleport(spawn.getLocation)
                    else sender.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission to teleport players to spawn.")
                else sender.sendMessage(format("spawn", "error.not-set"))
            }
        else return false
        true
    }
}
