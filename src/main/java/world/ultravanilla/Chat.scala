package world.ultravanilla

import net.luckperms.api.event.EventBus
import net.luckperms.api.event.user.track.UserPromoteEvent
import net.md_5.bungee.api.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.block.Block
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player._
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import world.ultravanilla.commands.{AfkCommand, MuteCommand}
import world.ultravanilla.reference.{Palette, Users}
import world.ultravanilla.serializable.{LoreItem, Position}
import world.ultravanilla.stuff.Range

import java.io.IOException
import java.util
import java.util.regex.Matcher
import java.util.regex.Pattern
import scala.jdk.CollectionConverters._
import scala.collection.mutable.ArrayBuffer

class Chat(val plugin: UltraVanilla) extends Listener {
    var history = ArrayBuffer[ChatEvent]()

    def sink(chatEvent: ChatEvent) = {
        history.addOne(chatEvent)
        // unimplemented
        ???
    }

    @EventHandler def onAsyncPlayerChat(event: AsyncPlayerChatEvent): Unit = {
        val player = event.getPlayer
        plugin.unsetAfk(player)
        val config = UltraVanilla.getPlayerConfig(player.getUniqueId)
        var message = event.getMessage

        // Mute
        if (config.getBoolean("muted", false)) {
            player.sendMessage(MuteCommand.COLOR + "You are muted.")
            event.setCancelled(true)
            plugin.getLogger.info(player.getName + " tried to say: " + message)
            return
        }
        // Chat color
        if (player.hasPermission("ultravanilla.chat.color")) message = Palette.translate(message)
        // Pings
        if (message.contains("@")) {
            val p = Pattern.compile("@([a-zA-Z0-9_]{2,})")
            val m = p.matcher(message)
            while ({
                m.find
            }) {
                val `match` = m.group(0)
                val name = m.group(1)
                if (name == "everyone" && player.hasPermission("ultravanilla.chat.everyone")) {
                    for (recipient <- event.getRecipients.asScala) {
                        message = message.replace(
                          `match`,
                          ChatColor.of(plugin.getConfig.getString("color.chat.ping.everyone")) + `match` + ChatColor.RESET
                        )
                        plugin.ping(recipient)
                    }
                } else {
                    for (recipient <- event.getRecipients.asScala) {
                        if (
                          recipient.getName.toLowerCase.contains(name.toLowerCase) || ChatColor
                              .stripColor(recipient.getDisplayName)
                              .toLowerCase
                              .contains(name)
                        ) {
                            message = message.replace(
                              `match`,
                              ChatColor.of(plugin.getConfig.getString("color.chat.ping.user")) + name + ChatColor.RESET
                            )
                            plugin.ping(player, recipient)
                        }
                    }
                }
            }
        }
        event.setMessage(message)
        // Chat formatter
        val donator = player.hasPermission("ultravanilla.donator")
        val staff = player.hasPermission("ultravanilla.staff-custom")
        val textPrefix = config.getString("text-prefix", ChatColor.RESET + "")
        val group = plugin.vault.getPrimaryGroup(player)
        val rankColor = plugin.getRoleColor(group) + ""
        val rank = plugin.getConfig.getString(
          "rename-groups." + group,
          group.substring(0, 1).toUpperCase + group.substring(1)
        )
        val donatorBracketsColor = ChatColor.of(plugin.getConfig.getString("color.chat.brackets.donator")) + ""
        val rankBracketsColor = ChatColor.of(plugin.getConfig.getString("color.chat.brackets.rank")) + ""
        val nameBracketsColor = ChatColor.of(plugin.getConfig.getString("color.chat.brackets.name")) + ""
        val defaultNameColor = ChatColor.of(plugin.getConfig.getString("color.chat.default-name-color")) + ""
        val staffColor = ChatColor.of(plugin.getConfig.getString("color.rank.staff")) + ""
        val format = String.format(
          "%s%s%s[%s%s%s] %s<%s%s> %s%s",
          if (donator)
              String.format(
                "%s[%sD%s] ",
                donatorBracketsColor,
                ChatColor.of(plugin.getConfig.getString("color.rank.donator")),
                donatorBracketsColor
              )
          else "",
          if (staff) String.format("%s[%sStaff%s] ", rankBracketsColor, staffColor, rankBracketsColor)
          else "",
          rankBracketsColor,
          rankColor,
          rank,
          rankBracketsColor,
          nameBracketsColor,
          defaultNameColor + "%1$s",
          nameBracketsColor,
          textPrefix,
          "%2$s"
        )
        val formatted = Palette.translate(format)
        event.setFormat(formatted)

        if (false) sink(ChatEvent(channel = 0, sender = player.getUniqueId(), source = ChatSource.InGame, staff = staff, donator = donator))
    }

}
