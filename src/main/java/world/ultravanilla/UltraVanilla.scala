package world.ultravanilla

import net.dv8tion.jda.api.{JDA, JDABuilder}
import net.luckperms.api.LuckPerms
import net.luckperms.api.model.user.User
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.InheritanceNode
import net.md_5.bungee.api.ChatColor
import org.bukkit.block.BlockFace
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.configuration.{InvalidConfigurationException, MemorySection}
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.{Bukkit, Location, OfflinePlayer, Sound}
import world.ultravanilla.commands._
import world.ultravanilla.reference.{Palette, Users}
import world.ultravanilla.serializable.{LoreItem, Position, Powertool, Title}
import world.ultravanilla.stuff.Range

import java.io.{File, FileOutputStream, IOException}
import java.util._
import scala.annotation.varargs
import scala.collection.JavaConverters._
import scala.io.Source;
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import org.bukkit

class UltraVanilla extends JavaPlugin {
    var luckPerms: LuckPerms = null

    var changelog: YamlConfiguration = null
    var storage: YamlConfiguration = null

    var motd = ""
    var configFile: File = null
    var random = new Random
    var staffActionsRecord: StaffActionsRecord = null

    var keepinvScoreboard: Scoreboard = null
    var keepinvOffTeam: Team = null

    var jda: JDA = null

    def getColoredRole(role: String) = ChatColor.of(
        getConfig.getString("color.rank." + role, "#ffffff")
    ) + getRoleCapitalized(role)

     def getRoleCapitalized(role: String) = role.substring(0, 1).toUpperCase + role.substring(1)

    @throws[IOException]
    def firstJoin(name: String) = {
        val joinFile = Source.fromFile(getDataFolder + "/join.txt")
        var count = 0
        for (line <- joinFile.getLines()) {
            val replacedLine = line.replace("${player.name}", name)
            getServer.dispatchCommand(Bukkit.getConsoleSender, replacedLine)
        }
    }

    def ping(sender: CommandSender, target: Player): Unit = {
        if (Users.isAFK(target)) sender.sendMessage(target.getName + " is AFK")
        ping(target)
    }

    def ping(target: Player) = target.playSound(
        target.getLocation,
        Sound.BLOCK_NOTE_BLOCK_PLING,
        1.0f,
        1.5f
    )

    def getStaffActionsRecord = staffActionsRecord

    def getTitle(title: String, color: ChatColor) =
        getString("title", "{title}", title, "$color", color + "")

    @varargs
    def getString(key: String, format: String*) = {
        var message = getConfig.getString("strings." + key)
        var i = 0
        while ({
            i < format.length
        }) {
            message = message.replace(format(i), format(i + 1))
            i += 2
        }
        Palette.translate(message)
    }

    def getString(key: String) =
        Palette.translate(getConfig.getString("strings." + key))

    def getCommandString(key: String) =
        getConfig.getString("strings.command." + key)

    @varargs
    def getRandomString(key: String, format: String*) = {
        val list = getConfig.getStringList("strings." + key)
        var message = list.get(random.nextInt(list.size - 1))
        var i = 0
        while ({
            i < format.length
        }) {
            message = message.replace(format(i), format(i + 1))
            i += 2
        }
        Palette.translate(message)
    }

    def sendMessageToStaff(message: String, permission: String) = {
        for (player <- getServer.getOnlinePlayers.asScala) {
            if (player.hasPermission(permission)) player.sendMessage(message)
        }
    }

    def removeFromStorage(key: String, value: String) = {
        val list = storage.getStringList(key)
        list.remove(value)
        storage.set(key, list)
    }

    def addToStorage(key: String, value: String) = {
        val list = storage.getStringList(key)
        list.add(value)
        storage.set(key, list)
    }

    def store(key: String, value: Any) = {
        storage.set(key, value)
        saveConfig(storage, "storage.yml")
    }

    def unsetAfk(player: Player) = if (Users.isAFK(player)) {
        Users.afk.remove(player.getUniqueId)
        getServer.broadcastMessage(player.getDisplayName + AfkCommand.COLOR + " is no longer AFK")
        player.setPlayerListName(player.getDisplayName)
    }

    def getNextRoleDate(player: OfflinePlayer) =
        player.getFirstPlayed + getConfig.getLong("times." + getNextRole(player))

    def getNextRole(player: OfflinePlayer): String = {
        val roles = getAllTimedRoles
        for (i <- 0 until roles.length - 1) {
            val role = roles(i)
            val nextRole = roles(i + 1)
            if (getRole(player) == role) return nextRole
        }
        roles(0)
    }

    def getRole(player: OfflinePlayer) =
        luckPerms.getUserManager.getUser(player.getUniqueId).getPrimaryGroup

    def getAllTimedRoles = getConfig
        .get("times")
        .asInstanceOf[MemorySection]
        .getKeys(false)
        .toArray(new Array[String](0))

    def async(task: Runnable) =
        Bukkit.getScheduler.runTaskAsynchronously(this, task)

    def getRoleColor(group: String) =
        ChatColor.of(getConfig.getString("color.rank." + group, "RESET"))

    def hasRightRole(player: OfflinePlayer) =
        getRole(player) == getRoleShouldHave(player)

    def getRoleShouldHave(player: OfflinePlayer) = {
        val roles = getAllTimedRoles
        var role = roles(0)
        val timePlayed = System.currentTimeMillis - player.getFirstPlayed
        for (key <- roles) {
            val roleTime = getConfig.getLong("times." + key)
            if (timePlayed >= roleTime) role = key
        }
        role
    }

    override def onEnable() = {
        UltraVanilla.instance = this

        staffActionsRecord = new StaffActionsRecord(this)

        // Add luckperms API
        val luckPermsProvider =
            Bukkit.getServicesManager.getRegistration(classOf[LuckPerms])
        if (luckPermsProvider == null)
            getLogger.warning("Could not link to LuckPerms.")
        else luckPerms = luckPermsProvider.getProvider
        ConfigurationSerialization.registerClass(
            classOf[Position],
            "com.akoot.plugins.ultravanilla.serializable.Position"
        )
        ConfigurationSerialization.registerClass(
            classOf[Powertool],
            "com.akoot.plugins.ultravanilla.serializable.Powertool"
        )
        ConfigurationSerialization.registerClass(
            classOf[Title],
            "com.akoot.plugins.ultravanilla.serializable.Title"
        )
        ConfigurationSerialization.registerClass(classOf[LoreItem], "LoreItem")

        getDataFolder.mkdir
        Users.DIR.mkdir
        loadConfigs()
        configFile = new File(getDataFolder, "config.yml")

        val motdList = getConfig.getStringList("motd.strings")
        scheduleSyncRepeatingTask(0L, 12 * 60 * 60 * 20L) { () =>
            setMOTD(motdList.get(random.nextInt(motdList.size)))
        }

        loadConfig(storage, "storage.yml")

        UltraVanilla.chat = new Chat(UltraVanilla.instance)

        // Spaghettatron!
        try {
            jda = JDABuilder.createDefault(getConfig.getString("discord.token")).build()
        } catch {
            case _: Throwable =>
        }

        getServer.getPluginManager.registerEvents(
            new world.ultravanilla.EventListener(UltraVanilla.instance),
            UltraVanilla.instance
        )
        getServer.getPluginManager.registerEvents(
            UltraVanilla.chat,
            UltraVanilla.instance
        )

        getCommand("nick").setExecutor(new NickCommand(UltraVanilla.instance))
        getCommand("suicide").setExecutor(new SuicideCommand(UltraVanilla.instance))
        // getCommand("make").setExecutor(new MakeCommand(UltraVanilla.instance))
        getCommand("gm").setExecutor(new GmCommand(UltraVanilla.instance))
        getCommand("title").setExecutor(new TitleCommand(UltraVanilla.instance))
        getCommand("reload").setExecutor(new ReloadCommand(UltraVanilla.instance))
        getCommand("ping").setExecutor(new PingCommand(UltraVanilla.instance))
        getCommand("raw").setExecutor(new RawCommand(UltraVanilla.instance))
        getCommand("motd").setExecutor(new MotdCommand(UltraVanilla.instance))
        val homeCommand = new HomeCommand(UltraVanilla.instance)
        getCommand("home").setExecutor(homeCommand)
        getCommand("h").setExecutor(homeCommand)
        val seenCommand = new SeenCommand(UltraVanilla.instance)
        getCommand("seen").setExecutor(seenCommand)
        getCommand("firstjoined").setExecutor(seenCommand)
        getCommand("lastseen").setExecutor(seenCommand)
        getCommand("spawn").setExecutor(new SpawnCommand(UltraVanilla.instance))
        getCommand("oldspawn").setExecutor(new OldSpawnCommand(UltraVanilla.instance))
        getCommand("keepinv").setExecutor(new KeepInvCommand(UltraVanilla.instance))
        getCommand("warp").setExecutor(new WarpCommand(UltraVanilla.instance))
        getCommand("setwarp").setExecutor(new SetWarpCommand(UltraVanilla.instance))
        getCommand("delwarp").setExecutor(new DelWarpCommand(UltraVanilla.instance))
        getCommand("print").setExecutor(new PrintCommand(UltraVanilla.instance))
        getCommand("do").setExecutor(new DoCommand(UltraVanilla.instance))
        getCommand("afk").setExecutor(new AfkCommand(UltraVanilla.instance))
        getCommand("msg").setExecutor(new MsgCommand(UltraVanilla.instance))
        getCommand("reply").setExecutor(new ReplyCommand(UltraVanilla.instance))
        getCommand("changelog").setExecutor(new ChangelogCommand(UltraVanilla.instance))
        getCommand("inventory").setExecutor(new InventoryCommand(UltraVanilla.instance))
        getCommand("lag").setExecutor(new LagCommand(UltraVanilla.instance))
        val customizeCommand = new CustomizeCommand(UltraVanilla.instance)
        getCommand("customize").setExecutor(customizeCommand)
        getCommand("rename").setExecutor(customizeCommand)
        getCommand("setlore").setExecutor(customizeCommand)
        getCommand("tptoggle").setExecutor(new TptoggleCommand(UltraVanilla.instance))
        getCommand("timezone").setExecutor(new TimezoneCommand(UltraVanilla.instance))
        getCommand("hat").setExecutor(new HatCommand(UltraVanilla.instance))
        getCommand("user").setExecutor(new UserCommand(UltraVanilla.instance))
        getCommand("smite").setExecutor(new SmiteCommand(UltraVanilla.instance))
        getCommand("back").setExecutor(new BackCommand(UltraVanilla.instance))
        getCommand("namecolor").setExecutor(new NameColorCommand(UltraVanilla.instance))
        getCommand("rolevariant").setExecutor(new RoleVariantCommand(UltraVanilla.instance))
        getCommand("playtime").setExecutor(new PlayTimeCommand(UltraVanilla.instance))
        getCommand("whois").setExecutor(new WhoIsCommand(UltraVanilla.instance))
        val muteCommand = new MuteCommand(UltraVanilla.instance)
        getCommand("mute").setExecutor(muteCommand)
        getCommand("smute").setExecutor(muteCommand)
        getCommand("unmute").setExecutor(muteCommand)
        getCommand("sunmute").setExecutor(muteCommand)
        getCommand("mcolor").setExecutor(new McolorCommand(UltraVanilla.instance))
        val signCommand = new SignCommand(UltraVanilla.instance)
        getCommand("sign").setExecutor(signCommand)
        getServer.getPluginManager.registerEvents(signCommand, UltraVanilla.instance)
        getCommand("promote").setExecutor(new PromoteCommand(UltraVanilla.instance))

        getCommand("tempban").setExecutor(new TempBanCommand(UltraVanilla.instance))
        getCommand("ban").setExecutor(new BanCommand(UltraVanilla.instance))
        getCommand("ban-ip").setExecutor(new BanIpCommand(UltraVanilla.instance))
        getCommand("kick").setExecutor(new KickCommand(UltraVanilla.instance))
        getCommand("banrollback").setExecutor(new PermabanCommand(UltraVanilla.instance))
        getCommand("pardon").setExecutor(new PardonCommand(UltraVanilla.instance))
        getCommand("warn").setExecutor(new WarnCommand(UltraVanilla.instance))
        getCommand("pardon-ip").setExecutor(new PardonIpCommand(UltraVanilla.instance))
        getCommand("rtp").setExecutor(new RtpCommand(UltraVanilla.instance))
        getCommand("setgroup").setExecutor(new SetGroupCommand(UltraVanilla.instance))
        getCommand("spectate").setExecutor(new SpectateCommand(UltraVanilla.instance))

        val scoreboardManager = Bukkit.getScoreboardManager
        keepinvScoreboard = scoreboardManager.getNewScoreboard
        keepinvOffTeam = keepinvScoreboard.registerNewTeam("KeepInventoryOFF")
        keepinvOffTeam.setPrefix("KeepInventory OFF ")
        keepinvOffTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS)
        keepinvOffTeam.setColor(bukkit.ChatColor.RED)
    }

    def setMOTD(motd: String) = this.motd = Palette.translate(motd)

    def loadConfigs() = {
        init("join.txt", false)
        getPluginConfig("config.yml", false)
        changelog = getPluginConfig("changelog.yml", true)
        storage = getPluginConfig("storage.yml", false)
        AnarchyRegion.configure(this)
    }

    def getPluginConfig(name: String, overwrite: Boolean) = {
        val config = new YamlConfiguration
        val configFile = new File(this.getDataFolder, name)
        init(name, overwrite)
        try config.load(configFile)
        catch {
            case e @ (_: IOException | _: InvalidConfigurationException) =>
                e.printStackTrace()
        }
        config
    }

    def init(name: String, overwrite: Boolean) = {
        val file = new File(this.getDataFolder, name)
        if (!file.exists || overwrite) {
            val fis = getClass.getResourceAsStream("/" + name)
            var fos: FileOutputStream = null
            try {
                fos = new FileOutputStream(file)
                val buf = new Array[Byte](1024)
                var i = 0
                while ({
                    i = fis.read(buf)
                    i != -1
                }) {
                    fos.write(buf, 0, i)
                }
            } catch {
                case e: Exception =>
                    e.printStackTrace()
            } finally try {
                if (fis != null) fis.close()
                if (fos != null) fos.close()
            } catch {
                case e: Exception =>
                    e.printStackTrace()
            }
        }
    }

    def loadConfig(config: YamlConfiguration, file: String) = {
        val configFile = new File(getDataFolder, file)
        try config.load(configFile)
        catch {
            case exception @ (_: IOException | _: InvalidConfigurationException) =>
                exception.printStackTrace()
        }
    }

    def scheduleSyncRepeatingTask(delay: Long, period: Long)(task: () => Unit) = {
        val runnable = new BukkitRunnable {
            override def run(): Unit = {
                task()
            }
        }
        runnable.runTaskTimer(this, delay, period)
    }

    def afk(player: Player) = {
        if (Users.isAFK(player)) {
            Users.afk.remove(player.getUniqueId)
            getServer.broadcastMessage(player.getDisplayName + AfkCommand.COLOR + " is no longer AFK")
            //TODO: implement this or something
            //            val chatEvent = new ChatEvent(
            //                getConfig.getLong("discord.server-chat"),
            //                player.getUniqueId,
            //                player.getDisplayName + " is no longer AFK",
            //                "",
            //                false,
            //                false,
            //                false,
            //                false,
            //                ChatSource.InGame)
        } else {
            Users.afk.add(player.getUniqueId)
            getServer.broadcastMessage(player.getDisplayName + AfkCommand.COLOR + " is now AFK")
        }
    }

    override def onDisable() = saveStorage()

    def saveStorage() = {
        saveConfig(storage, "storage.yml")
    }

    def saveConfig(config: YamlConfiguration, fileName: String) =
        try config.save(new File(getDataFolder, fileName))
        catch {
            case e: IOException =>
                e.printStackTrace()
        }

    // https://github.com/LuckPerms/api-cookbook/blob/master/src/main/java/me/lucko/lpcookbook/commands/SetGroupCommand.java
    def setGroup(player: OfflinePlayer, group: String) =
        luckPerms.getUserManager.modifyUser(
            player.getUniqueId,
            (user: User) => {
                def foo(user: User) = {
                    // Remove all other inherited groups the user had before.
                    user.data.clear(NodeType.INHERITANCE.matches(_))
                    user.data.remove(InheritanceNode.builder("default").build)
                    // Create a node to add to the player.
                    val node = InheritanceNode.builder(group).build
                    // Add the node to the user.
                    user.data.add(node)
                }

                foo(user)
            }
        )

    def getRandomOnlinePlayer = {
        val players = getServer.getOnlinePlayers
        players.asScala.toSeq(new Range(players.size).getRandom.toInt)
    }

    def scheduleSyncTask(delay: Long)(task: () => Unit) = {
        val runnable = new BukkitRunnable {
            override def run(): Unit = {
                task()
            }
        }
        runnable.runTaskLater(this, delay)
    }

    val cachedAutocompleteList = new ArrayList[String]
    var autocompleteLastUpdated = new Date(0)

    def offlineAutocompleteList(): ArrayList[String] = {
        if ((new Date().getTime - autocompleteLastUpdated.getTime) < 90 * 60 * 1000)
            return cachedAutocompleteList
        autocompleteLastUpdated = new Date

        for (player <- getServer.getOfflinePlayers) {
            val name = player.getName
            if (name != null && !cachedAutocompleteList.contains(name))
                cachedAutocompleteList.add(player.getName)
        }

        cachedAutocompleteList
    }
}

object UltraVanilla {
    var instance: UltraVanilla = null

    var chat: Chat = null

    def getInstance = instance

    def set(player: Player, key: String, value: Any): Unit =
        set(player.getUniqueId, key, value)

    def set(uid: UUID, key: String, value: Any) = {
        val config = getPlayerConfig(uid)
        if (config != null) {
            config.set(key, value)
            try config.save(getUserFile(uid))
            catch {
                case e: IOException =>
                    e.printStackTrace()
            }
        }
    }

    def set(player: OfflinePlayer, key: String, value: Any): Unit =
        set(player.getUniqueId, key, value)

    def add(uid: UUID, key: String, value: String) = {
        val list = getPlayerConfig(uid).getStringList(key)
        list.add(value)
        set(uid, key, list)
    }

    def remove(uid: UUID, key: String, value: String) = {
        val list = getPlayerConfig(uid).getStringList(key)
        list.remove(value)
        set(uid, key, list)
    }

    def isSuperAdmin(player: Player) =
        getPlayerConfig(player.getUniqueId).getBoolean("super-admin", false)

    def updateDisplayName(player: Player) = {
        val config = UltraVanilla.getPlayerConfig(player)
        var displayName = config.getString("display-name")
        if (displayName != null)
            displayName = ChatColor.valueOf(config.getString("name-color", "RESET")) + displayName
        player.setDisplayName(displayName)
        player.setPlayerListName(
            (if (displayName != null) displayName
             else player.getName) + (if (Users.isAFK(player)) " §7§o(AFK)"
                                     else "")
        )
    }

    def getPlayerConfig(player: OfflinePlayer): YamlConfiguration =
        getPlayerConfig(player.getUniqueId)

    def getPlayTime(player: OfflinePlayer) = {
        val difference = player.getLastSeen - player.getLastLogin
        UltraVanilla.getPlayerConfig(player).getLong("playtime", 0L) + difference
    }

    def updatePlaytime(player: OfflinePlayer) =
        UltraVanilla.set(player, "playtime", getPlayTime(player))

    def getPlayerConfig(uid: UUID): YamlConfiguration = {
        val config = new YamlConfiguration
        try config.load(getUserFile(uid))
        catch {
            case e @ (_: IOException | _: InvalidConfigurationException) =>
                return null
        }
        config
    }

    def getUserFile(uid: UUID) = {
        val userFile = new File(Users.DIR, uid.toString + ".yml")
        if (!userFile.exists)
            try userFile.createNewFile
            catch {
                case e: IOException =>
                    e.printStackTrace()
            }
        userFile
    }

    def isSafeLocation(location: Location): Boolean = {
        try {
            val feet = location.getBlock
            if (
                !feet.getType.isTransparent && !feet.getLocation
                    .add(0, 1, 0)
                    .getBlock
                    .getType
                    .isTransparent
            ) return false // not transparent (will suffocate)
            val head = feet.getRelative(BlockFace.UP)
            if (!head.getType.isTransparent) return false
            val ground = feet.getRelative(BlockFace.DOWN)
            // returns if the ground is solid or not.
            return ground.getType.isSolid
        } catch {
            case e: Exception =>
                e.printStackTrace()
        }
        false
    }

    def isStaff(sender: CommandSender) =
        sender.hasPermission("ultravanilla.permission.staff")

    def isAdmin(sender: CommandSender) =
        sender.hasPermission("ultravanilla.permission.admin")

    def killPlayer(player: Player, message: String) = {
        set(player.getUniqueId, "death-message", message)
        player.setHealth(0)
    }
}
