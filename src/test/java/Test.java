import com.akoot.plugins.ultravanilla.util.FormattedMessage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;


public class Test {

    public static void main(String[] args) {

        FormattedMessage message = new FormattedMessage();

        message.addComponent("#b2b2b2", "[");
        message.addComponent("#aeed38", "Loyalist");
        message.addComponent("#b2b2b2", "] <");
        message.addComponent("#93c8e2", "Akoot_");
//        message.addComponent("#9af4c6", "Ako");
//        message.addComponent("#515151", "ot_");
        message.addComponent("#b2b2b2", "> ");
        message.addComponent("#7fe5f9", "everyone");
        message.addComponent("#f7f7f7", "... ");
        message.addComponent("#7fe5f9", "obama");
        message.addComponent("#f7f7f7", " is here...");


        StringSelection stringSelection = new StringSelection("/tellraw @a " + message.getJson());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

    }
}
