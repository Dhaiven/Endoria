package game.pkg_Object;

import game.pkg_Entity.Sprite;

import javax.swing.*;
import java.awt.*;

public class PlaceableGameObject extends GameObject {

    protected JComponent paintedOn;
    protected Position position;

    public PlaceableGameObject(JComponent paintedOn, Sprite sprite, Position position) {
        super(sprite);
        this.paintedOn = paintedOn;
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public JComponent getPaintedOn() {
        return this.paintedOn;
    }

    public void paint(Graphics2D g2d) {
        if (!canPaint(g2d)) return;

        g2d.drawImage(sprite.get(), position.getX(), position.getY(), null);
    }
}
