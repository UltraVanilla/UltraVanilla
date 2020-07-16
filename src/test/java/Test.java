import com.akoot.plugins.ultravanilla.util.FormattedMessage;
import com.akoot.plugins.ultravanilla.util.RawComponent;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;


public class Test {

    public static void main(String[] args) {

        FormattedMessage formattedMessage = new FormattedMessage();

        String rank = "Winner";
        String rankColor = "#ff0000";
        String displayName = "#00bae7+b:Ako,#4f4f4f+ius:ot_";
        String message = "i want free diamonds NOW";

        // Donator status
        if (true) {
            formattedMessage.addComponents(RawComponent.parseColors("#b2b2b2:[,#ef7ac0:D,#b2b2b2:] "));
        }

        // Rank
        formattedMessage.addComponent("#b2b2b2", "[");
        formattedMessage.addComponent(rankColor, rank);
        formattedMessage.addComponent("#b2b2b2", "] ");

        // name
        formattedMessage.addComponent("#b2b2b2", "<");
        formattedMessage.addComponents(RawComponent.parseColors(displayName));
        formattedMessage.addComponent("#b2b2b2", "> ");

        // message
        formattedMessage.addComponent("#f4f4f4", message);

        StringSelection stringSelection = new StringSelection("/tellraw Akoot_ " + formattedMessage.getJson());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

    }
}
