import world.ultravanilla.reference.Palette;
import world.ultravanilla.stuff.StringUtil;
import net.md_5.bungee.api.ChatColor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringTests {

    String color = "#FF0000";
    String text = "HELLO world.";
    String testGradient = "§x§5§5§f§f§5§5t§x§5§5§f§f§8§0e§x§5§5§f§f§a§as§x§5§5§f§f§d§5t";

    @Test
    public void paletteTest() {
        String testText = Palette.translate("&" + color + text);
        assertEquals(ChatColor.of(color) + text, testText);
    }

    @Test
    public void hexTest() {
        color = "#00bae7";
        ChatColor col = ChatColor.of(color);
        assertEquals(color, Palette.getHex(col));
    }

    @Test
    public void timeTest() {
        int time = 2;
        assertEquals(time, StringUtil.getSeconds("40t"));
    }

//    @Test
//    public void colorTestChar() {
//        Color c = Color.decode("#00AAAA");
//        assertEquals(c, Colors.parse("3"));
//    }
//
//    @Test
//    public void colorTestString() {
//        Color c = Color.decode("#00AAAA");
//        assertEquals(c, Colors.parse("dark_aqua"));
//    }
//
//    @Test
//    public void colorTestHex() {
//        Color c = Color.decode("#00AAAA");
//        assertEquals(c, Colors.parse("#00AAAA"));
//    }
//
//    @Test
//    public void gradientTestChars() {
//        String actual = Palette.translate("&>a+btest");
//        assertEquals(testGradient, actual);
//    }
//
//    @Test
//    public void gradientTestMixed() {
//        String actual = Palette.translate("&>a+aquatest");
//        assertEquals(testGradient, actual);
//    }
//
//    @Test
//    public void gradientTestEnum() {
//        String actual = Palette.translate("&>green+aquatest");
//        assertEquals(testGradient, actual);
//    }
//
//    @Test
//    public void gradientTestHex() {
//        String actual = Palette.translate("&>#55FF55+#55FFFFtest");
//        assertEquals(testGradient, actual);
//    }
//
//    @Test
//    public void paletteTest2() {
//        String expected = "#4d3147";
//        Colors.testLoad();
//        assertEquals(ChatColor.of(expected), Colors.of("blackberry"));
//    }
}
