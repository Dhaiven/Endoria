package game.pkg_Image;

import java.util.HashMap;

public class Library {

    public static HashMap<Object, StaticSprite> sprites = new HashMap<>();

    public static StaticSprite get(Object id) {
        return sprites.get(id);
    }

    public static void register(Object id, StaticSprite sprite) {
        if (sprites.containsKey(id)) {
            throw new IllegalArgumentException("Sprite with id " + id + " already exists!");
        } else if (sprites.containsValue(sprite)) {
            throw new IllegalArgumentException("Sprite already exists!");
        }

        sprites.put(id, sprite);
    }
}
