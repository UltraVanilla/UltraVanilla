package world.ultravanilla.commands

import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import world.ultravanilla.AnarchyRegion
import world.ultravanilla.UltraVanilla

object DelWarpCommand {
    val COLOR = ChatColor.GREEN
}

class DelWarpCommand(val instance: UltraVanilla) extends UltraCommand(instance) with CommandExecutor {
    this.color = SpawnCommand.COLOR

    override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
        if (sender.isInstanceOf[Player] && args.length == 1) {
            val player = sender.asInstanceOf[Player]
            if (player.hasPermission("ultravanilla.command.warp.del")) {
                val warpName = args(0)
                player.sendMessage(format("warp", "message.del", "{name}", warpName))
                plugin.getConfig.set(s"warps.$warpName", null)
                plugin.saveConfig()
            } else sender.sendMessage(plugin.getString("no-permission", "{action}", "delete warps"))
        } else {
            sender.sendMessage(format("warp", "error.not-specified"))
        }
        true
    }
}
