package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.util.RawComponent;
import com.akoot.plugins.ultravanilla.util.RawMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class RawCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public RawCommand(Ultravanilla instance) {
        super(instance);
    }

    //TODO: actually send json messages
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            RawMessage message = new RawMessage();
            String[] rawComponents = String.join(" ", args).split("\\+");
            for (String text : rawComponents) {
                message.addComponent(getComponent(text));
            }
            Ultravanilla.tellRaw(message);
            return true;
        }
        return false;
    }

    private RawComponent getComponent(String syntax) {
        RawComponent component = new RawComponent();

        String[] args = refinedArgs(syntax.split(" "));
        String command = getArgFor(args, "-command");
        String suggestion = getArgFor(args, "-suggest");
        String link = getArgFor(args, "-link");
        String hover = getArgFor(args, "-hover");

        if (command != null) {
            component.setCommand(command);
        } else if (suggestion != null) {
            component.setSuggestion(suggestion);
        } else if (link != null) {
            component.setLink(link);
        }

        if (hover != null) {
            component.setHoverText(Palette.translate(hover));
        }

        component.setContent(Palette.translate(args[0]));

        return component;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
