package game.image;

import java.awt.*;

public interface AnimatedSpriteObserver {

    public void imageChanged(Image oldImage, Image newImage);
}
