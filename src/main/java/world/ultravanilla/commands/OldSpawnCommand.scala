package world.ultravanilla.commands

import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import world.ultravanilla.AnarchyRegion
import world.ultravanilla.UltraVanilla
import world.ultravanilla.serializable.Position

object OldSpawnCommand {
    val COLOR = ChatColor.GREEN
}

class OldSpawnCommand(val instance: UltraVanilla) extends UltraCommand(instance) with CommandExecutor {
    this.color = OldSpawnCommand.COLOR

    override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
        if (args.length == 0) if (sender.isInstanceOf[Player]) {
            val player = sender.asInstanceOf[Player]
            if (!AnarchyRegion.allowTeleport && AnarchyRegion.inside(player.getLocation)) return true
            val oldspawn = plugin.getConfig.get("oldspawn").asInstanceOf[Position]
            if (oldspawn != null) player.teleport(oldspawn.getLocation)
            else sender.sendMessage(format(command, "error.not-set"))
        } else sender.sendMessage(plugin.getString("player-only", "{action}", "set or go to the oldspawn"))
        else if (args.length == 1)
            if (args(0).equalsIgnoreCase("set")) if (sender.isInstanceOf[Player]) {
                val player = sender.asInstanceOf[Player]
                if (player.hasPermission("ultravanilla.command.oldspawn.set")) {
                    player.sendMessage(format(command, "Old spawn has been set"))
                    plugin.getConfig.set("oldspawn", new Position(player.getLocation))
                    plugin.saveConfig()
                } else sender.sendMessage(plugin.getString("no-permission", "{action}", "set Old Spawn"))
            } else sender.sendMessage(plugin.getString("player-only", "{action}", "set or go to the Old Spawn"))
            else {
                val oldspawn = plugin.getConfig.get("oldspawn").asInstanceOf[Position]
                val player = plugin.getServer.getPlayer(args(0))
                if (player == null) {
                    sender.sendMessage(plugin.getString("player-offline", "{player}", args(0)))
                    return true
                }
                if (oldspawn != null)
                    if (sender.hasPermission("ultravanilla.command.oldspawn.player")) player.teleport(oldspawn.getLocation)
                    else sender.sendMessage(ChatColor.RED + "You do not have permission to teleport players to Old Spawn.")
                else sender.sendMessage(format(command, "error.not-set"))
            }
        else return false
        true
    }
}
