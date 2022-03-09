package world.ultravanilla.commands

import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import world.ultravanilla.AnarchyRegion
import world.ultravanilla.UltraVanilla
import world.ultravanilla.serializable.Position

import scala.collection.JavaConverters._
import org.bukkit.command.TabExecutor
import org.bukkit.Bukkit

object KeepInvCommand {
    val COLOR = ChatColor.RED
}

class KeepInvCommand(val instance: UltraVanilla) extends UltraCommand(instance) with CommandExecutor with TabExecutor {
    this.color = KeepInvCommand.COLOR

    override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
        if (sender.isInstanceOf[Player]) {
            val player = sender.asInstanceOf[Player]

            val config = UltraVanilla.getPlayerConfig(player)

            var newStatus = true

            if (args.length == 0) {
                newStatus = !config.getBoolean("keepinv", true)
            } else if (args.length == 1) {
                newStatus = args(0) == "on" || args(0) == "true"
            } else return false

            UltraVanilla.set(player.getUniqueId, "keepinv", newStatus)
            sender.sendMessage(format("keepinv", s"message.$newStatus"))

            if (newStatus) 
                plugin.keepinvOffTeam.removePlayer(player)
            else {
                plugin.keepinvOffTeam.addPlayer(player)
                player.setScoreboard(plugin.keepinvScoreboard)
            }
        } else sender.sendMessage(plugin.getString("player-only", "{action}", "toggle keepinventory"))

        true
    }

    override def onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array[String]
    ): java.util.List[String] = {
        if (args.length < 2) {
            return getSuggestions(List("on", "off").asJava, args)
        }
        null
    }
}
