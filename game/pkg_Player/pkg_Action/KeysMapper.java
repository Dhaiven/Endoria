package game.pkg_Player.pkg_Action;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class KeysMapper {

    private static final Map<Integer, String> keyMap = new HashMap<>();

    static {
        // Lettres A-Z
        for (char c = 'A'; c <= 'Z'; c++) {
            keyMap.put((int) c, String.valueOf(c));
        }

        // Chiffres 0-9
        for (char c = '0'; c <= '9'; c++) {
            keyMap.put((int) c, String.valueOf(c));
        }

        // F 1-12
        for (int i = 1; i <= 12; i++) {
            keyMap.put(KeyEvent.VK_F1 + (i - 1), "F" + i);
        }

        // Caractères spéciaux
        keyMap.put(KeyEvent.VK_SPACE, "Space");
        keyMap.put(KeyEvent.VK_ENTER, "Enter");
        keyMap.put(KeyEvent.VK_ESCAPE, "Escape");
        keyMap.put(KeyEvent.VK_TAB, "Tab");
        keyMap.put(KeyEvent.VK_BACK_SPACE, "Backspace");
        keyMap.put(KeyEvent.VK_SHIFT, "Shift");
        keyMap.put(KeyEvent.VK_CONTROL, "Ctrl");
        keyMap.put(KeyEvent.VK_ALT, "Alt");
        keyMap.put(KeyEvent.VK_DELETE, "Delete");
        keyMap.put(KeyEvent.VK_UP, "Up");
        keyMap.put(KeyEvent.VK_DOWN, "Down");
        keyMap.put(KeyEvent.VK_LEFT, "Left");
        keyMap.put(KeyEvent.VK_RIGHT, "Right");

        // Symboles standards
        keyMap.put(KeyEvent.VK_COMMA, ",");
        keyMap.put(KeyEvent.VK_PERIOD, ".");
        keyMap.put(KeyEvent.VK_SLASH, "/");
        keyMap.put(KeyEvent.VK_BACK_SLASH, "\\");
        keyMap.put(KeyEvent.VK_SEMICOLON, ";");
        keyMap.put(KeyEvent.VK_EQUALS, "=");
        keyMap.put(KeyEvent.VK_MINUS, "-");
        keyMap.put(KeyEvent.VK_OPEN_BRACKET, "[");
        keyMap.put(KeyEvent.VK_CLOSE_BRACKET, "]");
        keyMap.put(KeyEvent.VK_QUOTE, "'");
        keyMap.put(KeyEvent.VK_BACK_QUOTE, "`");

        // Pavé numérique
        for (int i = 0; i <= 9; i++) {
            keyMap.put(KeyEvent.VK_NUMPAD0 + i, "Num" + i);
        }

        keyMap.put(KeyEvent.VK_ADD, "Num+");
        keyMap.put(KeyEvent.VK_SUBTRACT, "Num-");
        keyMap.put(KeyEvent.VK_MULTIPLY, "Num*");
        keyMap.put(KeyEvent.VK_DIVIDE, "Num/");
        keyMap.put(KeyEvent.VK_DECIMAL, "Num.");
    }

    public static String getKeyText(int keyCode) {
        return keyMap.getOrDefault(keyCode, "Unknown");
    }
}
