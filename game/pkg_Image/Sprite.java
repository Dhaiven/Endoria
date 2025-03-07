package game.pkg_Image;

import game.pkg_Util.Utils;

import java.awt.*;

public class Sprite {

    private final Image image;

    public Sprite(Image image) {
        if (image.getWidth(null) != Utils.TEXTURE_WIDTH || image.getHeight(null) != Utils.TEXTURE_HEIGHT) {
            image = image.getScaledInstance(
                    Utils.TEXTURE_WIDTH,
                    Utils.TEXTURE_HEIGHT,
                    Image.SCALE_DEFAULT
            );
        }

        this.image = image;
    }

    public Image get() {
        return image;
    }

    public int getWidth() {
        return image.getWidth(null);
    }

    public int getHeight() {
        return image.getHeight(null);
    }
}
