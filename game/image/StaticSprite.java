package game.image;

import game.object.Vector2i;
import game.util.Utils;

import java.awt.Image;

public class StaticSprite extends Sprite {

    private final Image image;

    public StaticSprite(Image image) {
        Vector2i textureSize = Utils.TEXTURE_SIZE;
        if (image.getWidth(null) != textureSize.x() || image.getHeight(null) != textureSize.y()) {
            image = image.getScaledInstance(
                    textureSize.x(),
                    textureSize.y(),
                    Image.SCALE_DEFAULT
            );
        }

        this.image = image;
    }

    public Image get() {
        return image;
    }
}
