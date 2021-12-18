package world.ultravanilla.commands


import net.md_5.bungee.api.ChatColor
import org.bukkit.command.{Command, CommandExecutor, CommandSender, TabExecutor}
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import world.ultravanilla.AnarchyRegion
import world.ultravanilla.UltraVanilla
import world.ultravanilla.serializable.Position

import java.util._
import scala.collection.JavaConverters._

object WarpCommand {
    val COLOR = ChatColor.GREEN
}

class WarpCommand(val instance: UltraVanilla) extends UltraCommand(instance) with CommandExecutor with TabExecutor {
    this.color = SpawnCommand.COLOR

    override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
        if (args.length >= 1) if (sender.isInstanceOf[Player]) {
            val player = sender.asInstanceOf[Player]
            if (!AnarchyRegion.allowTeleport && AnarchyRegion.inside(player.getLocation)) return true

            val name = args(0)
            val location = plugin.getConfig.get(s"warps.$name").asInstanceOf[Position]
            if (location != null) player.teleport(location.getLocation)
            else sender.sendMessage(format("warp", "error.not-set", "{name}", name))
        } else {
            sender.sendMessage(plugin.getString("player-only", "{action}", "warp"))
        }
        else {
            sender.sendMessage(format("warp", "error.not-specified"))
        }
        true
    }

    override def onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array[String]): java.util.List[String] = {
        if (args.length < 2) {
            val suggestions = new ArrayList[String]
            return getSuggestions(plugin.getConfig.getConfigurationSection("warps").getKeys(true).asScala.toList.asJava, args)
        }
        null
    }
}
