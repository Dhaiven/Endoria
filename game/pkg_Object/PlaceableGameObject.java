package game.pkg_Object;

import game.pkg_Image.StaticSprite;

import javax.swing.*;
import java.awt.*;

public class PlaceableGameObject extends GameObject {

    protected JComponent paintedOn;
    protected Position position;
    protected final int layer;

    public PlaceableGameObject(JComponent paintedOn, StaticSprite sprite, Position position, int layer) {
        super(sprite);
        this.paintedOn = paintedOn;
        this.position = position;
        this.layer = layer;
    }

    public Position getPosition() {
        return position;
    }

    public JComponent getPaintedOn() {
        return this.paintedOn;
    }

    public void paint(Graphics2D g2d) {
        if (!canPaint(g2d)) return;

        g2d.drawImage(sprite.get(), (int) position.x() - sprite.getWidth() / 2, (int) position.y() - sprite.getHeight() / 2, null);
    }
}
