package game.pkg_Image;

import java.util.HashMap;

public class Library {

    public static HashMap<Object, Sprite> sprites = new HashMap<>();

    public static Sprite get(Object id) {
        return sprites.get(id);
    }

    public static void register(Object id, Sprite sprite) {
        if (sprites.containsKey(id)) {
            throw new IllegalArgumentException("Sprite with id " + id + " already exists!");
        } else if (sprites.containsValue(sprite)) {
            throw new IllegalArgumentException("Sprite already exists!");
        }

        sprites.put(id, sprite);
    }
}
