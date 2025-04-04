package game.pkg_Image;

import java.awt.Image;

public abstract class Sprite {

    public abstract Image get();

    public int getWidth() {
        return get().getWidth(null);
    }

    public int getHeight() {
        return get().getHeight(null);
    }
}
