package game.pkg_Loader;

import game.pkg_Tile.Tile;
import game.pkg_Tile.TileProperty;

public class LoaderUtils {

    public static <T extends Enum<T>> T loadEnum(Object object, Class<T> enumType) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }

        String value = object.toString().trim();

        for (T constant : enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(value)) {
                return constant;
            }
        }

        return null;
    }

    public static TileProperty loadTileProperty(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }

        String value = object.toString().trim();

        for (TileProperty constant : TileProperty.values()) {
            if (constant.getId().equalsIgnoreCase(value)) {
                return constant;
            }
        }

        return null;
    }
}
