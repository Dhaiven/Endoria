package game.tile.property;

import java.util.List;

public final class BooleanPropertyType extends BaseTileProperty<Boolean> {

    private final BooleanPropertyValue FALSE = new BooleanPropertyValue(false);
    private final BooleanPropertyValue TRUE = new BooleanPropertyValue(true);

    private BooleanPropertyType(String name, Boolean defaultData) {
        super(name, List.of(true, false), defaultData);
    }

    public static BooleanPropertyType of(String name, Boolean defaultData) {
        return new BooleanPropertyType(name, defaultData);
    }

    @Override
    public BooleanPropertyValue createValue(Boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public BooleanPropertyValue tryCreateValue(Object value) {
        if (value instanceof Boolean bool) {
            return bool ? TRUE : FALSE;
        } else if (value instanceof Number number) {
            var intValue = number.intValue();
            if (intValue == 0 || intValue == 1) {
                return intValue == 1 ? TRUE : FALSE;
            }
        } else if (value instanceof String string) {
            if (string.equalsIgnoreCase("true")) return TRUE;
            if (string.equalsIgnoreCase("false")) return FALSE;
        }
        throw new IllegalArgumentException("Invalid value for boolean property type: " + value);
    }

    public final class BooleanPropertyValue extends TilePropertyValue<Boolean, BooleanPropertyType, Byte> {

        BooleanPropertyValue(Boolean value) {
            super(BooleanPropertyType.this, value);
        }

        @Override
        public Byte getSerializedValue() {
            return (byte) (value ? 1 : 0);
        }

        @Override
        public String toString() {
            return "BoolPropertyValue(name=" + name + ", value=" + value + ")";
        }
    }
}
