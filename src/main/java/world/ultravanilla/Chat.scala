package world.ultravanilla

import net.md_5.bungee.api.ChatColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.`object`.ObjectContents
import org.bukkit.event.player._
import org.bukkit.event.{EventHandler, Listener}
import world.ultravanilla.commands.MuteCommand
import world.ultravanilla.reference.Palette

import java.io.{File, FileReader}
import java.util.UUID
import java.util.regex.Pattern
import scala.collection.mutable.{ArrayBuffer, Map => MutableMap}
import scala.jdk.CollectionConverters._
import scala.util.matching.Regex
import org.json.simple.parser.JSONParser

class Chat(val plugin: UltraVanilla) extends Listener {
    var history = ArrayBuffer[ChatEvent]()
    private val emojiPath = new File("src/main/java/world/ultravanilla/emojis.json")
    private val emojiConfig: MutableMap[String, SpriteDef] = loadEmojiJson()

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

        // --- Sprite shortcodes ---
        val spriteComponent = applySpriteShortcodes(message)
        event.message(spriteComponent)

        // Chat formatter
        val donator = player.hasPermission("ultravanilla.donator")
        val staff = player.hasPermission("ultravanilla.staff-custom")
        val textPrefix = config.getString("text-prefix", ChatColor.RESET + "")
        val group = plugin.getRole(player)
        val rankColor = plugin.getRoleColor(group) + ""
        val renames = plugin.getConfig.getStringList("rename-groups." + group)

        var rank = if (renames.size == 0) {
            plugin.getConfig.getString(
                "rename-groups." + group,
                group.substring(0, 1).toUpperCase + group.substring(1)
            )
        } else {
            val variant = UltraVanilla.getPlayerConfig(player).getInt("role-variant", 0)
            renames.get(variant)
        }

        val donatorBracketsColor = ChatColor.of(plugin.getConfig.getString("color.chat.brackets.donator")) + ""
        val staffBracketsColor = ChatColor.of(plugin.getConfig.getString("color.chat.brackets.staff")) + ""
        val rankBracketsColor = ChatColor.of(plugin.getConfig.getString("color.chat.brackets.rank")) + ""
        val nameBracketsColor = ChatColor.of(plugin.getConfig.getString("color.chat.brackets.name")) + ""
        val defaultNameColor = ChatColor.of(plugin.getConfig.getString("color.chat.default-name-color")) + ""
        val staffColor = ChatColor.of(plugin.getConfig.getString("color.rank.staff")) + ""

        val donatorSymbol = plugin.getConfig.getString("rename-groups.donator", "D")
        val staffSymbol = plugin.getConfig.getString("rename-groups.staff", "S")

        val format = String.format(
            "%s%s%s[%s%s%s] %s<%s%s> %s%s",
            if (donator)
                String.format(
                    "%s[%s%s%s] ",
                    donatorBracketsColor,
                    ChatColor.of(plugin.getConfig.getString("color.rank.donator")),
                    donatorSymbol,
                    donatorBracketsColor
                )
            else "",
            if (staff) String.format("%s[%s%s%s] ", staffBracketsColor, staffColor, staffSymbol, staffBracketsColor)
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

    // --- Sprite Parser ---
    private def applySpriteShortcodes(message: String): Component = {
        val tokenPattern: Regex = """:minecraft:([a-z0-9_\-/]+):""".r
        var base: Component = Component.empty()
        var lastEnd = 0
        val matches = tokenPattern.findAllMatchIn(message).toList
    
        if (matches.isEmpty) return Component.text(message)
    
        for (m <- matches) {
            base = base.append(Component.text(message.substring(lastEnd, m.start)))
            val itemKey = m.group(1).toLowerCase
            base = base.append(spriteFor(itemKey))
            lastEnd = m.end
        }
        base.append(Component.text(message.substring(lastEnd)))
    }

    private def spriteFor(key: String): Component = {
        try {
            return Component.`object`(ObjectContents.sprite(Key.key(s"minecraft:item/$key")))
        } catch {
            case _: Exception => // ignore and fall back
        }

        // Custom head sprites from local JSON
        val defnOpt = Option(emojiConfig.get(key))
        if (defnOpt.isDefined && defnOpt.get.`type`.equalsIgnoreCase("head")) {
            val defn = defnOpt.get
            val name = Option(defn.name).getOrElse(key)
            val uuid = Option(defn.uuid).getOrElse(name)
            if (uuid.matches("^[0-9a-fA-F-]{36}$")) {
                return Component.`object`(ObjectContents.playerHead(UUID.fromString(uuid)))
            } else {
                return Component.`object`(ObjectContents.playerHead(name))
            }
        }

        // Fallback text
        Component.text(s":$key:")
    }

    // --- JSON Loader ---
    private def loadEmojiJson(): MutableMap[String, SpriteDef] = {
        val map = MutableMap[String, SpriteDef]()
        if (!emojiPath.exists()) {
            plugin.getLogger.warning(s"emojis.json not found at ${emojiPath.getPath}")
            return map
        }

        try {
            val parser = new JSONParser()
            val obj = parser.parse(new FileReader(emojiPath)).asInstanceOf[java.util.Map[String, java.util.Map[String, String]]]
            obj.asScala.foreach { case (key, v) =>
                val t = v.getOrDefault("type", "item")
                val id = v.getOrDefault("id", null)
                val name = v.getOrDefault("name", null)
                val uuid = v.getOrDefault("uuid", null)
                map.put(key, SpriteDef(t, id, name, uuid))
            }
        } catch {
            case e: Exception =>
                plugin.getLogger.warning(s"Failed to load emojis.json: ${e.getMessage}")
        }
        map
    }

    def sink(chatEvent: ChatEvent) = {
        history.addOne(chatEvent)
        // unimplemented
        ???
    }

    // --- Data Class ---
    case class SpriteDef(`type`: String, id: String = null, name: String = null, uuid: String = null)
}
