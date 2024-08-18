package world.ultravanilla.commands

import net.md_5.bungee.api.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.Statistic
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import world.ultravanilla.UltraVanilla
import world.ultravanilla.reference.Palette
import world.ultravanilla.stuff.StringUtil
import java.{util => ju}

object PlayTimeCommand {
    val COLOR: ChatColor = ChatColor.of("#8DF6C8")
}

class PlayTimeCommand(val instance: UltraVanilla) extends UltraCommand(instance) with CommandExecutor with TabExecutor {
    this.color = PlayTimeCommand.COLOR

    def getPlayTime(player: OfflinePlayer): String = {
        StringUtil.getTimeString(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20L * 1000L)
    }

    override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
        var player = if (args.length == 0) {
            if (!sender.isInstanceOf[Player]) {
                sender.sendMessage(plugin.getString("player-only", "{action}", "check your playtime"))
                return true
            }
            sender.asInstanceOf[Player]
        } else if (args.length == 1) {
            plugin.getServer.getOfflinePlayer(args(0))
        } else return false

        if (player == null || !(player.hasPlayedBefore || player.isOnline)) {
            sender.sendMessage(plugin.getString("player-unknown", "{player}", args(0)))
            return true
        }

        sender.sendMessage(
            String.valueOf(Palette.NOUN) + player.getName + PlayTimeCommand.COLOR + " has played for " + Palette.NUMBER + getPlayTime(player)
        )
        true
    }

    override def onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array[String]): ju.List[String] = {
        getSuggestions(plugin.offlineAutocompleteList(), args)
    }
}
