package game.object;

import game.image.Sprite;
import java.awt.Graphics2D;

public class GameObject {

    protected Sprite sprite;

    public GameObject(Sprite sprite) {
        this.sprite = sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public int getWidth() {
        return sprite.getWidth();
    }

    public int getHeight() {
        return sprite.getHeight();
    }

    public boolean canPaint(Graphics2D g2d) {
        return true;
    }
}
