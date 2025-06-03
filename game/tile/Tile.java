package game.tile;

import game.GameEngine;
import game.image.Sprite;
import game.object.GameObject;
import game.object.Identifier;
import game.object.Vector2i;
import game.tile.behavior.TileBehavior;
import game.tile.property.TileProperty;
import game.util.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Tile extends GameObject {

    private final Identifier id;

    private final Collision[] collisions;

    private final List<TileBehavior> behaviors;
    private final List<TileProperty.TilePropertyValue<?, ?, ?>> properties = new ArrayList<>();

    public Tile(Identifier id, Sprite sprite) {
        this(id, sprite, new Collision[0], new ArrayList<>());
    }

    public Tile(Identifier id, Sprite sprite, Collision[] collisions, List<TileProperty.TilePropertyValue<?, ?, ?>> properties) {
        super(sprite);
        this.id = id;

        this.collisions = collisions;

        this.properties.addAll(properties);

        this.behaviors = GameEngine.getInstance().getTileManager().getTileBehaviors(this);
    }

    public Identifier getId() {
        return id;
    }

    public Collision[] getCollisions() {
        return collisions;
    }

    public boolean hasCollisions() {
        return collisions.length > 0;
    }

    public List<TileBehavior> getBehaviors() {
        return behaviors;
    }

    public void paint(Graphics2D g2d, Vector2i position) {
        if (!canPaint(g2d)) return;

        if (position.x() < 0 || position.y() < 0) {
            throw new IndexOutOfBoundsException("Position out of bounds!");
        }
        if (sprite.getWidth() != Utils.TEXTURE_SIZE.x() || sprite.getHeight() != Utils.TEXTURE_SIZE.y()) {
            throw new IndexOutOfBoundsException("Sprite size out of bounds!");
        }

        g2d.drawImage(sprite.get(), position.x(), position.y(), null);
    }

    /**
     * Gets the property values of this state.
     *
     * @return unmodifiable map of {@link TileProperty} to {@link TileProperty.TilePropertyValue}
     */
    public Map<TileProperty<?>, TileProperty.TilePropertyValue<?, ?, ?>> getPropertyValues() {
        var hashMap = new LinkedHashMap<TileProperty<?>, TileProperty.TilePropertyValue<?, ?, ?>>();
        for (var propertyValue : properties) {
            hashMap.put(propertyValue.getPropertyType(), propertyValue);
        }

        return Collections.unmodifiableMap(hashMap);
    }

    /**
     * Creates a new state with updated property values.
     *
     * @param propertyValues list of {@link TileProperty.TilePropertyValue} to set
     *
     * @throws IllegalArgumentException if any value is unsupported by this block type
     */
    public Tile setPropertyValues(List<TileProperty.TilePropertyValue<?, ?, ?>> propertyValues) {
        propertyValues.forEach(this::setPropertyValue);
        return this;
    }

    public <DATATYPE, PROPERTY extends TileProperty<DATATYPE>> boolean hasPropertyValue(PROPERTY property) {
        for (var p : properties) {
            if (p.getPropertyType() == property) {
                return true;
            }
        }

        return false;
    }

    public boolean hasPropertyValue(TileProperty.TilePropertyValue<?, ?, ?> propertyValue) {
        return properties.contains(propertyValue);
    }

    /**
     * Gets the value of a specific property.
     *
     * @param property   the {@link TileProperty} to query
     * @param <DATATYPE> property value type
     * @param <PROPERTY> property type subtype
     *
     * @return the property value
     *
     * @throws IllegalArgumentException if property is unsupported by this block type
     */
    public <DATATYPE, PROPERTY extends TileProperty<DATATYPE>> DATATYPE getPropertyValue(PROPERTY property) {
        for (var p : properties) {
            if (p.getPropertyType() == property) {
                return (DATATYPE) p.getValue();
            }
        }

        return null;
    }

    /**
     * Creates a new state with an updated property value.
     *
     * @param propertyValue the {@link TileProperty.TilePropertyValue} to apply
     *
     * @throws IllegalArgumentException if value is unsupported by this block type
     */
    public Tile setPropertyValue(TileProperty.TilePropertyValue<?, ?, ?> propertyValue) {
        for (int i  = 0; i < properties.size(); i++) {
            var p = properties.get(i);
            if (p.getPropertyType() == propertyValue.getPropertyType()) {
                properties.set(i, propertyValue);
                return this;
            }
        }

        properties.add(propertyValue);
        return this;
    }

    /**
     * Creates a new state with a specific property value set.
     *
     * @param property   the {@link TileProperty} to modify
     * @param value      the value to set
     * @param <DATATYPE> property value type
     * @param <PROPERTY> property type subtype
     *
     * @throws IllegalArgumentException if property or value is unsupported by this block type
     */
    public <DATATYPE, PROPERTY extends TileProperty<DATATYPE>> Tile setPropertyValue(PROPERTY property, DATATYPE value) {
        return setPropertyValue(property.createValue(value));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tile tile)) return false;
        return Objects.equals(getId(), tile.getId()) &&
                Objects.deepEquals(getCollisions(), tile.getCollisions()) &&
                Objects.equals(getBehaviors(), tile.getBehaviors()) &&
                Objects.equals(properties, tile.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), Arrays.hashCode(getCollisions()), getBehaviors(), properties);
    }

    @Override
    public String toString() {
        return "Tile{" +
                "id=" + id +
                ", collisions=" + Arrays.toString(collisions) +
                ", behaviors=" + behaviors +
                ", properties=" + properties +
                '}';
    }
}
