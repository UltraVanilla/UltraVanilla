package world.ultravanilla.commands

import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import world.ultravanilla.AnarchyRegion
import world.ultravanilla.UltraVanilla
import world.ultravanilla.serializable.Position

object SetWarpCommand {
    val COLOR = ChatColor.GREEN
}

class SetWarpCommand(val instance: UltraVanilla) extends UltraCommand(instance) with CommandExecutor {
    this.color = SpawnCommand.COLOR

    override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
        //else if (args.length == 1)
        if (sender.isInstanceOf[Player] && args.length == 1) {
            val player = sender.asInstanceOf[Player]
            if (player.hasPermission("ultravanilla.command.warp.set")) {
                val warpName = args(0)
                player.sendMessage(format("warp", "message.set", "{name}", warpName))
                plugin.getConfig.set(s"warps.$warpName", new Position(player.getLocation))
                plugin.saveConfig()
            } else sender.sendMessage(plugin.getString("no-permission", "{action}", "set warps"))
        } else {
            sender.sendMessage(format("warp", "error.not-specified"))
        }
        true
    }
}
