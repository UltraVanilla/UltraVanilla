package world.ultravanilla.commands;

import world.ultravanilla.AnarchyRegion;
import world.ultravanilla.UltraVanilla;
import world.ultravanilla.reference.LegacyColors;
import world.ultravanilla.reference.Palette;
import world.ultravanilla.reference.Users;
import world.ultravanilla.serializable.Position;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import world.ultravanilla.UltraVanilla;
import world.ultravanilla.reference.Users;
import world.ultravanilla.serializable.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpectateCommand extends UltraCommand implements CommandExecutor {

    public static final ChatColor COLOR = ChatColor.WHITE;

    public SpectateCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            UUID uuid = player.getUniqueId();

            if (Users.spectators.contains(uuid)) {
                player.teleport(((Position) plugin.getConfig().get("spawn")).getLocation());
                player.setGameMode(GameMode.SURVIVAL);
                Users.spectators.remove(player.getUniqueId());
                UltraVanilla.set(player, "spectator", false);
            } else {
                player.teleport(AnarchyRegion.center());
                player.setGameMode(GameMode.SPECTATOR);
                Users.spectators.add(player.getUniqueId());
                UltraVanilla.set(player, "spectator", true);
            }
        }

        return true;
    }

}
