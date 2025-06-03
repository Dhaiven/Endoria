package game.image;

import java.awt.Image;
import java.util.List;
import java.util.Map;

public class AnimatedSprite extends Sprite {

    private final List<Map.Entry<Sprite, Double>> sprites;
    private int spriteIndex;

    private AnimatedSpriteObserver observer = null;

    public AnimatedSprite(List<Map.Entry<Sprite, Double>> sprites) {
        this(sprites, 0);
    }

    public AnimatedSprite(List<Map.Entry<Sprite, Double>> sprites, int firstSpriteIndex) {
        this(sprites, firstSpriteIndex, null);
    }

    public AnimatedSprite(List<Map.Entry<Sprite, Double>> sprites, int firstSpriteIndex, AnimatedSpriteObserver observer) {
        this.sprites = sprites;
        this.spriteIndex = firstSpriteIndex;
    }

    public List<Map.Entry<Sprite, Double>> getSprites() {
        return sprites;
    }

    @Override
    public Image get() {
        return this.sprites.get(this.spriteIndex).getKey().get();
    }

    public void setSpriteIndex(int spriteIndex) {
        this.spriteIndex = spriteIndex;
    }

    public AnimatedSpriteObserver getObserver() {
        return observer;
    }

    public void setObserver(AnimatedSpriteObserver observer) {
        this.observer = observer;
    }
}
