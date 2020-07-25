package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RawCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public RawCommand(UltraVanilla instance) {
        super(instance);
    }

    //TODO: actually send json messages
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            TextComponent textComponent = new TextComponent();
            String[] rawComponents = String.join(" ", args).split("\\+");
            for (String text : rawComponents) {
                textComponent.addExtra(getComponent(text));
            }
            List<Player> players = new ArrayList<>(plugin.getServer().getOnlinePlayers());
            String playersString = getArgFor(args, "-to");
            if (playersString != null) {
                players = getPlayers(playersString);
            }
            for (Player player : players) {
                player.sendMessage(textComponent);
            }
            return true;
        }
        return false;
    }

    private TextComponent getComponent(String syntax) {
        TextComponent component = new TextComponent();

        String[] args = refinedArgs(syntax.split(" "));
        String command = getArgFor(args, "-command");
        String suggestion = getArgFor(args, "-suggest");
        String link = getArgFor(args, "-link");
        String hover = getArgFor(args, "-hover");

        if (command != null) {
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        } else if (suggestion != null) {
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestion));
        } else if (link != null) {
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        }

        if (hover != null) {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
        }

        component.setText(Palette.translate(args[0]));

        return component;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> params = Arrays.asList(args);
        if (args.length > 0) {
            suggestions.add("+");
            if (!(params.contains("-link") || params.contains("-command") || params.contains("-suggest"))) {
                suggestions.add("-link");
                suggestions.add("-command");
                suggestions.add("-suggest");
            }
            if (!(params.contains("-item") || params.contains("-hover"))) {
                suggestions.add("-item");
                suggestions.add("-hover");
            }
            if (!params.contains("-to")) {
                suggestions.add("-to");
            }
        }
        return getSuggestions(suggestions, args);
    }
}
