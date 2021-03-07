package world.ultravanilla.commands

import world.ultravanilla.AnarchyRegion
import world.ultravanilla.UltraVanilla
import world.ultravanilla.reference.LegacyColors
import world.ultravanilla.reference.Palette
import world.ultravanilla.reference.Users
import world.ultravanilla.serializable.Position
import net.md_5.bungee.api.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util
import java.util.UUID

object SpectateCommand { val COLOR = ChatColor.WHITE }
class SpectateCommand(val instance: UltraVanilla) extends UltraCommand(instance) with CommandExecutor {
  this.color = SpectateCommand.COLOR
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]) = {
    if (sender.isInstanceOf[Player]) {
      val player = sender.asInstanceOf[Player]
      val uuid = player.getUniqueId
      if (Users.spectators.contains(uuid)) {
        val spawn = plugin.getConfig.get("spawn").asInstanceOf[Position].getLocation

        player.setGameMode(GameMode.SURVIVAL)
        player.teleport(spawn)
        Users.spectators.remove(player.getUniqueId)
        UltraVanilla.set(player, "spectator", false)

        plugin.scheduleSyncTask(1) { () =>
          player.setGameMode(GameMode.SURVIVAL)
          player.teleport(spawn)
        }
      } else {
        player.setGameMode(GameMode.SPECTATOR)
        player.teleport(AnarchyRegion.center)
        Users.spectators.add(player.getUniqueId)
        UltraVanilla.set(player, "spectator", true)
      }
    }
    true
  }
}
