package game.player.action;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class KeysMapper {

    private static final Map<Integer, String> keyMap = new HashMap<>();

    static {
        Field[] fields = KeyEvent.class.getDeclaredFields();

        for (Field field : fields) {
            if (!field.getName().startsWith("VK_")) continue;

            try {
                int keyCode = field.getInt(null); // static field
                String keyName = field.getName().substring(3); // Supprime "VK_"

                // Met un format plus lisible
                keyName = keyName.replace('_', ' ');
                keyName = keyName.substring(0, 1).toUpperCase() + keyName.substring(1).toLowerCase();

                keyMap.put(keyCode, keyName);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getKeyText(int keyCode) {
        return keyMap.getOrDefault(keyCode, "Unknown");
    }
}
