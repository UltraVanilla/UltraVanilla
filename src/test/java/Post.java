import com.akoot.plugins.ultravanilla.reference.Palette;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Post {

    public static void main(String[] args) throws IOException, InvalidConfigurationException {
        YamlConfiguration changelog = new YamlConfiguration();
        changelog.load(new File("src/main/resources/changelog.yml"));
        for (String key : changelog.getKeys(true)) {
            if (key.contains(".")) {
                printValues(changelog, key);
            }
        }
    }

    public static void printValues(YamlConfiguration changelog, String key) {
        for (String value : changelog.getStringList(key)) {
            String keyFinal = key.substring(key.lastIndexOf(".") + 1);
            keyFinal = String.valueOf(keyFinal.charAt(0)).toUpperCase() + keyFinal.substring(1);
            value = value
                    .replaceAll("/[\\w-]+", "`$0`");
            value = Palette.translate(value);
            value = ChatColor.stripColor(value);
            System.out.println("* " + keyFinal + " " + value);
        }
    }
}
