package game.tile.property;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;

public final class EnumPropertyType<T extends Enum<T>> extends BaseTileProperty<T> {

    private final EnumMap<T, EnumPropertyValue> cachedValues;
    private final Class<T> enumClass;

    private EnumPropertyType(String name, Class<T> enumClass, T defaultData) {
        super(name, Arrays.asList(enumClass.getEnumConstants()), defaultData);

        this.enumClass = enumClass;
        var map = new HashMap<T, EnumPropertyValue>();
        for (var value : validValues) {
            map.put(value, new EnumPropertyValue(value));
        }
        cachedValues = new EnumMap<>(map);
    }

    public static <T extends Enum<T>> EnumPropertyType<T> of(String name, Class<T> enumClass, T defaultData) {
        return new EnumPropertyType<>(name, enumClass, defaultData);
    }

    @Override
    public EnumPropertyValue createValue(T value) {
        return cachedValues.get(value);
    }

    @Override
    public EnumPropertyValue tryCreateValue(Object value) {
        if (enumClass.isInstance(value)) {
            return cachedValues.get(enumClass.cast(value));
        } else if (value instanceof String str) {
            return cachedValues.get(Enum.valueOf(enumClass, str.toUpperCase()));
        }
        throw new IllegalArgumentException("Invalid value for enum property type: " + value);
    }

    public final class EnumPropertyValue extends TileProperty.TilePropertyValue<T, EnumPropertyType<T>, String> {

        EnumPropertyValue(T value) {
            super(EnumPropertyType.this, value);
        }

        @Override
        public String getSerializedValue() {
            return value.name().toLowerCase();
        }

        @Override
        public String toString() {
            return "EnumPropertyValue(name=" + name + ", value=" + value + ")";
        }
    }
}
