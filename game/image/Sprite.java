package game.image;

import java.awt.Image;

public abstract class Sprite {

    public abstract Image get();

    public int getWidth() {
        return get().getWidth(null);
    }

    public int getHeight() {
        return get().getHeight(null);
    }

    @Override
    public String toString() {
        return "Image: " + get();
    }
}
