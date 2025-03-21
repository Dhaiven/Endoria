package game.pkg_Tile;

import game.pkg_Image.Sprite;
import game.pkg_Object.GameObject;
import game.pkg_Object.Vector2;
import game.pkg_Tile.behavior.TileBehavior;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Tile extends GameObject {

    // TODO: add behavior
    private final int id;
    private final Shape collision;
    private final List<TileBehavior> behaviors = new ArrayList<>();

    public Tile(int id, Sprite sprite, Shape collision) {
        super(sprite);
        this.id = id;
        this.collision = collision;
    }

    public int getId() {
        return id;
    }

    public Shape getCollision() {
        return collision;
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
