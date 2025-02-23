package game.pkg_Tile;

import game.pkg_Object.GameObject;
import game.pkg_Object.Position;
import game.pkg_Entity.Sprite;
import game.pkg_Object.Vector2;
import game.pkg_Tile.behavior.TileBehavior;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Tile extends GameObject {

    // TODO: add behavior
    private final List<TileBehavior> behaviors = new ArrayList<>();

    public Tile(Sprite sprite) {
        super(sprite);
    }

    public List<TileBehavior> getBehaviors() {
        return behaviors;
    }

    public void paint(Graphics2D g2d, Vector2 position) {
        if (!canPaint(g2d)) return;

        g2d.drawImage(sprite.get(), position.x(), position.y(), null);
    }
}
