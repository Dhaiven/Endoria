package game.tile.property;

import java.util.List;
import java.util.Objects;

public abstract class BaseTileProperty<DATATYPE> implements TileProperty<DATATYPE> {

    protected final String name;
    protected final List<DATATYPE> validValues;
    protected final DATATYPE defaultValue;

    protected BaseTileProperty(String name, List<DATATYPE> validValues, DATATYPE defaultValue) {
        this.name = name;
        this.validValues = validValues;
        this.defaultValue = Objects.requireNonNull(defaultValue);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<DATATYPE> getValidValues() {
        return validValues;
    }

    @Override
    public DATATYPE getDefaultValue() {
        return defaultValue;
    }
}
