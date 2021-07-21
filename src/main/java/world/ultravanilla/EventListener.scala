package world.ultravanilla

import net.luckperms.api.event.EventBus
import net.luckperms.api.event.user.track.UserPromoteEvent
import net.md_5.bungee.api.ChatColor
import org.bukkit.event.block.{BlockBreakEvent, BlockPlaceEvent}
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import org.bukkit.event.player._
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.{GameMode, Location, Material}
import world.ultravanilla.reference.{Palette, Users}
import world.ultravanilla.serializable.{LoreItem, Position}
import world.ultravanilla.stuff.Range

import java.io.IOException
import java.util
import java.util.regex.Pattern
import scala.jdk.CollectionConverters._

class EventListener(val plugin: UltraVanilla) extends Listener {
    val eventBus: EventBus = plugin.luckPerms.getEventBus
    eventBus.subscribe(classOf[UserPromoteEvent], this.onUserPromote)
    val loreWhitelist = Set(Material.PLAYER_HEAD, Material.REDSTONE_TORCH, Material.REDSTONE_WALL_TORCH, Material.WALL_TORCH, Material.TORCH)

    def onUserPromote(event: UserPromoteEvent): Unit =
        UltraVanilla.set(event.getUser.getUniqueId, "last-promotion", System.currentTimeMillis)

    // Might break with future versions
    @EventHandler def onPlayerTeleport(event: PlayerTeleportEvent): Unit = {
        val player = event.getPlayer
        if (Users.isSpectator(player)) spectatorCheck(event)
        if (!UltraVanilla.isSuperAdmin(player))
            if (event.getCause == TeleportCause.COMMAND || event.getCause == TeleportCause.SPECTATE) {
                plugin.getServer.getOnlinePlayers.forEach { p => }
                for (p <- plugin.getServer.getOnlinePlayers.asScala) {
                    if (p.getLocation == event.getTo) {
                        val config = UltraVanilla.getPlayerConfig(p.getUniqueId)
                        if (config != null && config.getBoolean(Users.TP_DISABLED, false)) event.setCancelled(true)
                    }
                }
            }
        UltraVanilla.set(player, "last-teleport", new Position(event.getFrom))
    }

    def spectatorCheck(event: PlayerMoveEvent): Unit =
        if (!AnarchyRegion.inside(event.getTo)) {
            val player = event.getPlayer
            val spawn = plugin.getConfig.get("spawn").asInstanceOf[Position].getLocation

            // the ultimate rubberbanding code

            val destination = event.getFrom
            val attemptedLocation = event.getTo

            destination setPitch attemptedLocation.getPitch
            destination setYaw attemptedLocation.getYaw + 180.0f

            event.setTo(destination)
        }

    @EventHandler def onCommandType(e: PlayerCommandPreprocessEvent): Unit = {
        var message = e.getMessage
        var pattern = Pattern.compile("\\$\\{([\\w]+)\\.([\\w-.]+)}")
        var matcher = pattern.matcher(message)
        while ( {
            matcher.find
        })
            message = message.replace(matcher.group, getValue(matcher.group(1), matcher.group(2).toLowerCase.split("\\.")))
        // !<command>[(<args>)]
        pattern = Pattern.compile("!([\\w]+)(?:\\(([^)]+)\\))?")
        matcher = pattern.matcher(message)
        while ( {
            matcher.find
        })
            message = message.replaceFirst(Pattern.quote(matcher.group), `macro`(matcher.group(1), matcher.group(2)) + "")
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

    def `macro`(command: String, args: String): String = {
        System.out.println(command + ":" + args)
        if (command.equalsIgnoreCase("randp"))
            if (args == null) return plugin.getRandomOnlinePlayer.getName
            else if (command.equalsIgnoreCase("rands")) if (args != null) {
                val strings = args.split(",")
                return strings(new Range(strings.length).getRandom.toInt)
            } else if (command.startsWith("rand")) {
                val range =
                    if (args == null) new Range(0, 1)
                    else stuff.Range.of(args)
                if (range == null) return null
                val random = range.getRandom
                if (command.endsWith("f")) return random + ""
                else return String.format("%.0f", random)
            }
        null
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

    @EventHandler def onPlayerMove(event: PlayerMoveEvent): Unit = {
        val player = event.getPlayer
        plugin.unsetAfk(player)
        if (Users.isSpectator(player)) spectatorCheck(event)
    }

    @EventHandler def onServerListPing(event: ServerListPingEvent): Unit = {
        var version = plugin.getServer.getVersion
        version = version.substring(version.indexOf("MC: ") + 4, version.length - 1)
        event.setMotd(
            Palette.translate(plugin.getConfig.getString("motd.server-name")) + " " + ChatColor.of(
                plugin.getConfig.getString("motd.version-color")
            ) + version + "\n" + ChatColor.RESET + plugin.motd
        )
    }

    @EventHandler def onPlayerJoin(event: PlayerJoinEvent): Unit = {
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
        if (loreWhitelist contains block.getState.getType) if (loreItems != null && !loreItems.isEmpty) {
            for (loreItem <- loreItems.asScala) {
                // TODO: figure out why rewriting in scala necessitated this jank
                // TODO: maybe just rework the serialization
                val loreItemPosition = loreItem.getPosition
                if (
                    loreItemPosition.getX == location.getX
                        && loreItemPosition.getY == location.getY
                        && loreItemPosition.getZ == location.getZ
                        && loreItemPosition.getWorld == location.getWorld.getUID
                ) {
                    event.setDropItems(false)
                    val itemStack = block.getDrops.iterator.next
                    val meta = itemStack.getItemMeta
                    val name = loreItem.getName
                    if (name.nonEmpty) meta.setDisplayName(name)
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
        if (loreWhitelist contains event.getItemInHand.getType)
            if (meta != null && !(meta.getDisplayName.isEmpty && (meta.getLore != null && meta.getLore.isEmpty))) {
                val item =
                    new LoreItem(meta.getDisplayName, meta.getLore, new Position(event.getBlockPlaced.getLocation))
                var loreItems = plugin.storage.getList("lore-items").asInstanceOf[util.List[LoreItem]]
                if (loreItems == null) loreItems = new util.ArrayList[LoreItem]
                loreItems.add(item)
                plugin.store("lore-items", loreItems)
            }
    }

    @EventHandler def onPortalCreate(event: PortalCreateEvent) = {
        if (event.getReason == PortalCreateEvent.CreateReason.END_PLATFORM) {
            val world = event.getWorld

            // create a 2nd end platform suitable for farming
            val square = (y: Int, material: Material) => for (x <- 198 to 202) for (z <- -2 to 2) {
                val location = new Location(world, x, y, z)
                if (!location.getChunk.isLoaded)
                    location.getChunk.load()

                val block = world.getBlockAt(location)
                block setType material
            }

            square(48, Material.OBSIDIAN)
            square(49, Material.AIR)
            square(50, Material.AIR)
            square(51, Material.AIR)
        }
    }
}
