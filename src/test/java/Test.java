import com.akoot.plugins.ultravanilla.reference.Palette;
import com.akoot.plugins.ultravanilla.util.RawComponent;
import com.akoot.plugins.ultravanilla.util.RawMessage;

public class Test {

    public static void main(String[] args) {
        String test = Palette.translate("&6hey -s &dClickHere -url https://google.com -hover &7DoIt -s &7how are you");

        RawMessage message = new RawMessage();

        String[] objects = test.split("-s ");
        for (String object : objects) {
            RawComponent text = new RawComponent();
            String url = "";
            String suggestion = "";
            String command = "";
            String hover = "";
            String[] items = object.split(" ");

            for (int i = 0; i < items.length; i++) {
                int next = i + 1;
                if (items[i].equalsIgnoreCase("-url")) {
                } else if (items[i].equalsIgnoreCase("-suggest")) {
                } else if (items[i].equalsIgnoreCase("-command")) {
                } else if (items[i].equalsIgnoreCase("-hover")) {
                } else {
                    text.setContent(items[i]);
                }
            }

            if (!url.isEmpty()) {
                text.setLink(url);
            } else if (!suggestion.isEmpty()) {
                text.setSuggestion(suggestion);
            } else if (!command.isEmpty()) {
                text.setCommand(command);
            }

            if (!hover.isEmpty()) {
                text.setHoverText(hover);
            }
            text.setLink(url);
            message.addComponent(text);
        }

        System.out.println(message.getJSON());
    }
}
