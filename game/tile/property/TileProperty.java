package game.tile.property;

import java.util.List;

public interface TileProperty<DATATYPE> {

    String getName();

    DATATYPE getDefaultValue();
    List<DATATYPE> getValidValues();

    TilePropertyValue<DATATYPE, ? extends TileProperty<DATATYPE>, ?> createValue(DATATYPE value);

    TilePropertyValue<DATATYPE, ? extends TileProperty<DATATYPE>, ?> tryCreateValue(Object value);

    /**
     * Create a default value for this property type.
     *
     * @return the value
     */
    default TilePropertyValue<DATATYPE, ? extends TileProperty<DATATYPE>, ?> createDefaultValue() {
        return createValue(getDefaultValue());
    }


    abstract class TilePropertyValue<DATATYPE, PROPERTY extends TileProperty<DATATYPE>, SERIALIZED_DATATYPE> {

        protected final PROPERTY propertyType;
        protected final DATATYPE value;

        public TilePropertyValue(PROPERTY propertyType, DATATYPE value) {
            this.propertyType = propertyType;
            this.value = value;
        }

        public PROPERTY getPropertyType() {
            return propertyType;
        }

        public DATATYPE getValue() {
            return value;
        }

        /**
         * Get the serialized value of this property.
         * <p>
         * Different from the value, the serialized value is the value that will be stored in the block state nbt.
         * For example, the value of a boolean property is a boolean, but the serialized value is a byte.
         *
         * @return the serialized value.
         */
        public abstract SERIALIZED_DATATYPE getSerializedValue();

        @Override
        public String toString() {
            return propertyType.getName() + "=" + getSerializedValue();
        }
    }
}
