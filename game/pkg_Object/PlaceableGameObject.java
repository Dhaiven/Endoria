package game.pkg_Object;

import game.pkg_Image.Sprite;
import java.awt.Graphics2D;

public class PlaceableGameObject extends GameObject {

    protected Position position;
    protected int layer;

    public PlaceableGameObject(Sprite sprite, Position position, int layer) {
        super(sprite);
        this.position = position;
        this.layer = layer;
    }

    public Position getPosition() {
        return position;
    }

    public void paint(Graphics2D g2d) {
        if (!canPaint(g2d)) return;

        g2d.drawImage(sprite.get(), (int) position.x() - sprite.getWidth() / 2, (int) position.y() - sprite.getHeight(), null);
    }
}
