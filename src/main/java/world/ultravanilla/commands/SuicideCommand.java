package world.ultravanilla.commands;

import world.ultravanilla.UltraVanilla;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SuicideCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.of("#b22345");
    public static final ChatColor PLAYER = ChatColor.of("#3772ff");

    public SuicideCommand(UltraVanilla instance) {
        super(instance);
        color = COLOR;
    }

    private boolean isInPact(Player suicider, Player player) {
        String pactId = UltraVanilla.getPlayerConfig(suicider.getUniqueId()).getString("suicide-pact");
        if (pactId != null) {
            return player.getUniqueId().equals(UUID.fromString(pactId));
        }
        return false;
    }

    public void suicide(Player player, String message) {
        if (UltraVanilla.getPlayerConfig(player).getLong("last-suicide") + (plugin.getConfig().getLong("suicide-time") * 1000L) <= System.currentTimeMillis()) {
            UltraVanilla.killPlayer(player, message);
            UltraVanilla.set(player, "last-suicide", System.currentTimeMillis());
            String pactId = UltraVanilla.getPlayerConfig(player.getUniqueId()).getString("suicide-pact");
            if (pactId != null && !pactId.isEmpty()) {
                player = plugin.getServer().getPlayer(UUID.fromString(pactId));
                if (player != null) {
                    UltraVanilla.killPlayer(player, message);
                }
            }
        } else {
            player.sendMessage(COLOR + "You must wait to suicide again!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            String message;
            Player player = (Player) sender;
            if (args.length == 0) {
                message = plugin.getRandomString("suicide-messages", "{player}", PLAYER + player.getName() + COLOR);
            } else {
                message = PLAYER + player.getName() + COLOR + " " + String.join(" ", args);
            }
            suicide(player, message);
        } else {
            sender.sendMessage(plugin.getString("player-only", "{action}", "kill yourself"));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
