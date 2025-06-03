package game.tile.property;

import java.util.stream.IntStream;

public final class IntPropertyType extends BaseTileProperty<Integer> {

    private final IntPropertyValue[] cachedValues;
    private final int min, max;

    private IntPropertyType(String name, int min, int max, Integer defaultData) {
        super(name, IntStream.range(min, max + 1).boxed().toList(), defaultData);
        this.min = min;
        this.max = max;

        this.cachedValues = new IntPropertyValue[max + 1 - min];
        for (int i = min; i <= max; i++) {
            this.cachedValues[i] = new IntPropertyValue(i);
        }
    }

    public static IntPropertyType of(String name, int min, int max, Integer defaultData) {
        return new IntPropertyType(name, min, max, defaultData);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public IntPropertyValue createValue(Integer value) {
        return cachedValues[value - min];
    }

    @Override
    public IntPropertyValue tryCreateValue(Object value) {
        if (value instanceof Number number) {
            return cachedValues[number.intValue() - min];
        } else if (value instanceof String string) {
            int intValue;
            try {
                intValue = Integer.parseInt(string);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid value for int property type: " + value);
            }
            return cachedValues[intValue - min];
        }
        throw new IllegalArgumentException("Invalid value for int property type: " + value);
    }

    public final class IntPropertyValue extends TilePropertyValue<Integer, IntPropertyType, Integer> {

        IntPropertyValue(Integer value) {
            super(IntPropertyType.this, value);
        }

        @Override
        public Integer getSerializedValue() {
            return value;
        }

        @Override
        public String toString() {
            return "IntPropertyValue(name=" + name + ", value=" + value + ")";
        }
    }
}
