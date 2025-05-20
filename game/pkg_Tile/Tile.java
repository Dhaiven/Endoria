package game.pkg_Tile;

import game.GameEngineV2;
import game.pkg_Image.AnimatedSprite;
import game.pkg_Image.Sprite;
import game.pkg_Object.DrawType;
import game.pkg_Object.GameObject;
import game.pkg_Object.Vector2;
import game.pkg_Tile.behavior.TileBehavior;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Tile extends GameObject {

    private final int id;

    private final Shape[] collisions;
    private final CollisionType collisionType;

    private final List<TileBehavior> behaviors;
    private final List<TileProperty> properties;
    private final DrawType drawType;

    public Tile(int id, Sprite sprite) {
        this(id, sprite, CollisionType.IF_SAME_LAYER, new Shape[0], new ArrayList<>(), DrawType.UNDER);
    }

    public Tile(int id, Sprite sprite, CollisionType collisionType, Shape[] collisions, List<TileProperty> properties, DrawType drawType) {
        super(sprite);
        this.id = id;

        this.collisions = collisions;
        this.collisionType = collisionType;

        this.properties = properties;
        this.drawType = drawType;

        // TODO: pas redéfinir le tilemaneger, surement le mattre dans le GameEngine
        this.behaviors = (new TileManager()).getTileBehaviors(this);
    }

    public int getId() {
        return id;
    }

    public CollisionType getCollisionType() {
        return collisionType;
    }

    public Shape[] getCollisions() {
        return collisions;
    }

    public boolean hasCollisions() {
        return collisions.length > 0;
    }

    public List<TileProperty> getProperties() {
        return properties;
    }

    public boolean hasProperty(TileProperty property) {
        return properties.contains(property);
    }

    public DrawType getDrawType() {
        return this.drawType;
    }

    public List<TileBehavior> getBehaviors() {
        return behaviors;
    }

    public void paint(Graphics2D g2d, Vector2 position) {
        if (!canPaint(g2d)) return;

        if (position.x() < 0 || position.y() < 0) {
            throw new IndexOutOfBoundsException("Position out of bounds!");
        }

        g2d.drawImage(sprite.get(), (int) position.x(), (int) position.y(), null);
    }

    @Override
    public String toString() {
        return "Tile{" +
                "sprite=" + sprite +
                "behaviors=" + behaviors +
                '}';
    }
}
