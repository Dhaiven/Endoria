package game.loader;

import game.tile.property.TileProperties;
import game.tile.property.TileProperty;

import java.lang.reflect.Field;

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

    public static TileProperty.TilePropertyValue<?, ?, ?> loadTileProperty(String name, Object value) throws IllegalAccessException {
        Field[] fields = TileProperties.class.getDeclaredFields();

        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(name)) {
                var fieldValue = field.get(null);
                if (fieldValue instanceof TileProperty<?> tileProperty) {
                    return tileProperty.tryCreateValue(value);
                }

                return null;
            }
        }

        return null;
    }
}
