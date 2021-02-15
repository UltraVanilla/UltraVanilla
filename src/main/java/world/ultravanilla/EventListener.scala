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

class EventListener(val plugin: UltraVanilla) extends Listener { // get the LuckPerms event bus
  val eventBus = plugin.luckPerms.getEventBus
  eventBus.subscribe(classOf[UserPromoteEvent], this.onUserPromote)

  private def onUserPromote(event: UserPromoteEvent) =
    UltraVanilla.set(event.getUser.getUniqueId, "last-promotion", System.currentTimeMillis)

  private def unsetAfk(player: Player) = if (Users.isAFK(player)) {
    Users.afk.remove(player.getUniqueId)
    plugin.getServer.broadcastMessage(player.getDisplayName + AfkCommand.COLOR + " is no longer AFK")
    player.setPlayerListName(player.getDisplayName)
  }

  // Might break with future versions
  @EventHandler def onPlayerTeleport(event: PlayerTeleportEvent) = {
    val player = event.getPlayer
    if (!UltraVanilla.isSuperAdmin(player))
      if (event.getCause.name == "COMMAND" || event.getCause.name == "SPECTATE") {

        plugin.getServer.getOnlinePlayers.forEach { p => }
        for (p <- plugin.getServer.getOnlinePlayers.asScala) {
          if (p.getLocation == event.getTo) {
            val config = UltraVanilla.getPlayerConfig(p.getUniqueId)
            if (config != null && config.getBoolean(Users.TP_DISABLED, false)) event.setCancelled(true)
          }
        }
      }
    UltraVanilla.set(player, "last-teleport", new Position(event.getFrom))
    if (Users.isSpectator(player)) spectatorCheck(event.getTo, player)
  }

  private def `macro`(command: String, args: String): String = {
    System.out.println(command + ":" + args)
    if (command.equalsIgnoreCase("randp"))
      if (args == null) return plugin.getRandomOnlinePlayer.getName
      else if (command.equalsIgnoreCase("rands")) if (args != null) {
        val strings = args.split(",")
        return strings(new Range(strings.length).getRandom.toInt)
      } else if (command.startsWith("rand")) {
        var range =
          if (args == null) new Range(0, 1)
          else stuff.Range.of(args)
        if (range == null) return null
        val random = range.getRandom
        if (command.endsWith("f")) return random + ""
        else return String.format("%.0f", random)
      }
    null
  }

  @EventHandler def onCommandType(e: PlayerCommandPreprocessEvent) = {
    var message = e.getMessage
    var pattern = Pattern.compile("\\$\\{([\\w]+)\\.([\\w-.]+)}")
    var matcher = pattern.matcher(message)
    while ({
      matcher.find
    })
      message =
        message.replace(matcher.group, getValue(matcher.group(1), matcher.group(2).toLowerCase.split("\\.")))
    // !<command>[(<args>)]
    pattern = Pattern.compile("!([\\w]+)(?:\\(([^)]+)\\))?")
    matcher = pattern.matcher(message)
    while ({
      matcher.find
    })
      message =
        message.replaceFirst(Pattern.quote(matcher.group), `macro`(matcher.group(1), matcher.group(2)) + "")
    // --as
    if (message.contains("--as ")) {
      val command = message.substring(1, message.indexOf("--as ") - 1)
      val target = message.substring(message.indexOf("--as ") + "--as ".length)
      message = "/make " + target + " do " + command
    }
    // --at
    if (message.contains("--at ")) {
      val command = message.substring(1, message.indexOf("--at ") - 1)
      val location = message.substring(message.indexOf("--at ") + "--at ".length)
      message = "/execute at " + location + " run " + command
    }
    // --in
    if (message.contains("--in ")) {
      val command = message.substring(1, message.indexOf("--in ") - 1)
      val world = message.substring(message.indexOf("--in ") + "--in ".length)
      message = "/execute in " + world + " run " + command
    }
    e.setMessage(message)
  }

  def getValue(parent: String, children: Array[String]): String = {
    if (parent == "player") if (children.length == 2) {
      val player = plugin.getServer.getOfflinePlayer(children(0))
      if (player.hasPlayedBefore || player.isOnline)
        if (children(1).matches("(nick|display|custom)[-_]?name"))
          if (UltraVanilla.getPlayerConfig(player).contains("display-name"))
            return UltraVanilla.getPlayerConfig(player).getString("display-name") + ChatColor.RESET
          else if (children(1).matches("role|rank|group")) return plugin.getRole(player)
          else if (children(1).matches("next[_-]?(role|rank|group)")) return plugin.getNextRole(player)
          else if (children(1) == "uuid") return player.getUniqueId.toString
    } else if (parent == "color") if (children.length == 2) {
      val theClass = children(0)
      if (theClass.matches("rank|role|group")) {
        val roleColor = plugin.getRoleColor(children(1))
        if (roleColor != null) return Palette.untranslate(roleColor + "")
      }
    } else if (children.length == 1) {
      val color = ChatColor.of(children(0))
      if (color != null) return Palette.untranslate(color + "")
    }
    parent + "." + children.mkString(",")
  }

  @EventHandler def onPlayerMove(event: PlayerMoveEvent) = {
    val player = event.getPlayer
    unsetAfk(player)
    if (Users.isSpectator(player)) spectatorCheck(event.getTo, player)
  }

  private[ultravanilla] def spectatorCheck(to: Location, player: Player) = if (!AnarchyRegion.inside(to)) {
    Users.spectators.remove(player.getUniqueId)
    player.teleport(plugin.getConfig.get("spawn").asInstanceOf[Position].getLocation)
    player.setGameMode(GameMode.SURVIVAL)
    UltraVanilla.set(player, "spectator", false)
  }

  @EventHandler def onServerListPing(event: ServerListPingEvent) = {
    var version = plugin.getServer.getVersion
    version = version.substring(version.indexOf("MC: ") + 4, version.length - 1)
    event.setMotd(
      Palette.translate(plugin.getConfig.getString("motd.server-name")) + " " + ChatColor.of(
        plugin.getConfig.getString("motd.version-color")
      ) + version + "\n" + ChatColor.RESET + plugin.motd
    )
  }

  @EventHandler def onPlayerJoin(event: PlayerJoinEvent) = {
    val player = event.getPlayer
    val nick = UltraVanilla.getPlayerConfig(player.getUniqueId).getString("display-name")
    if (nick != null) {
      UltraVanilla.updateDisplayName(player)
      event.setJoinMessage(
        event.getJoinMessage.replace(player.getName, player.getDisplayName + ChatColor.YELLOW)
      )
    }
    if (!player.hasPlayedBefore) {
      val spawn = plugin.getConfig.get("spawn").asInstanceOf[Position]
      if (spawn != null) player.teleport(spawn.getLocation)
      UltraVanilla.set(player, Users.FIRST_LOGIN, System.currentTimeMillis)
      try plugin.firstJoin(player.getName)
      catch {
        case e: IOException =>
          e.printStackTrace()
      }

      val helperRole = plugin.getConfig.getLong("discord.helper-role")
      val channel = plugin.jda.getTextChannelById(plugin.getConfig.getLong("discord.private-reports-channel"))
      channel
        .sendMessage("<@&" + helperRole + "> " + player.getName() + " has logged in for the first time")
        .queue();
    }

    val thisVersion = plugin.getDescription.getVersion
    val lastVersion = UltraVanilla.getPlayerConfig(player.getUniqueId).getString("last-version")
    if (lastVersion == null || !(lastVersion == thisVersion) && player.hasPlayedBefore)
      player.performCommand("changelog")
    UltraVanilla.set(player, "last-version", thisVersion)
    if (UltraVanilla.getPlayerConfig(player).getBoolean("spectator")) {
      player.teleport(plugin.getConfig.get("spawn").asInstanceOf[Position].getLocation)
      player.setGameMode(GameMode.SURVIVAL)
      Users.spectators.remove(player.getUniqueId)
      UltraVanilla.set(player, "spectator", false)
    }
  }

  @EventHandler def onPlayerLeave(event: PlayerQuitEvent) = {
    val player = event.getPlayer
    UltraVanilla.set(player.getUniqueId, Users.LOGOUT_LOCATION, new Position(event.getPlayer.getLocation))
    UltraVanilla.updatePlaytime(player)
    event.setQuitMessage(
      event.getQuitMessage.replace(player.getName, player.getDisplayName + ChatColor.YELLOW)
    )
  }

  @EventHandler def onPlayerDeath(event: PlayerDeathEvent) = {
    val player = event.getEntity
    if (player.hasPermission("ultravanilla.command.suicide")) {
      val message = event.getDeathMessage
      if (message != null && message.endsWith(" died"))
        event.setDeathMessage(UltraVanilla.getPlayerConfig(player.getUniqueId).getString("death-message"))
    }

    if (AnarchyRegion.inside(player.getLocation) && player.getGameMode() == GameMode.SURVIVAL) {
      event.setKeepInventory(false)
      event.setKeepLevel(false)
    } else {
      event.setKeepInventory(true)
      event.setKeepLevel(true)
      event.getDrops().clear()
      event.setDroppedExp(0)
    }
  }

  @EventHandler def onBlockBreak(event: BlockBreakEvent): Unit = {
    val block = event.getBlock
    val location = block.getLocation
    val loreItems = plugin.storage.getList("lore-items").asInstanceOf[util.List[LoreItem]]
    if (block.getState.getType eq Material.PLAYER_HEAD) if (loreItems != null && !loreItems.isEmpty) {
      for (loreItem <- loreItems.asScala) {
        if (loreItem != null && loreItem.getPosition == location) {
          event.setDropItems(false)
          val itemStack = block.getDrops.iterator.next
          val meta = itemStack.getItemMeta
          val name = loreItem.getName
          if (!name.isEmpty) meta.setDisplayName(name)
          val lore = loreItem.getLore
          if (lore != null) meta.setLore(lore)
          itemStack.setItemMeta(meta)
          block.getWorld.dropItem(event.getBlock.getLocation, itemStack)
          loreItems.remove(loreItem)
          plugin.store("lore-items", loreItems)
          return
        }
      }
    }
  }

  @EventHandler def onBlockPlace(event: BlockPlaceEvent) = {
    val meta = event.getItemInHand.getItemMeta
    if (event.getItemInHand.getType eq Material.PLAYER_HEAD)
      if (meta != null && !(meta.getDisplayName.isEmpty && (meta.getLore != null && meta.getLore.isEmpty))) {
        val item =
          new LoreItem(meta.getDisplayName, meta.getLore, new Position(event.getBlockPlaced.getLocation))
        var loreItems = plugin.storage.getList("lore-items").asInstanceOf[util.List[LoreItem]]
        if (loreItems == null) loreItems = new util.ArrayList[LoreItem]
        loreItems.add(item)
        plugin.store("lore-items", loreItems)
      }
  }

  @EventHandler def onAsyncPlayerChat(event: AsyncPlayerChatEvent): Unit = {
    val player = event.getPlayer
    unsetAfk(player)
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
    //ignored
    for (p <- event.getRecipients.asScala) {
      if (UltraVanilla.isIgnored(p, player)) {
        event.getRecipients.remove(p)
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
  }
}
