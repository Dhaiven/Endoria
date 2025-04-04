package game.pkg_Image;

import game.GameEngineV2;
import game.pkg_Scheduler.Task;

import java.awt.Image;
import java.util.List;
import java.util.Map;

public class AnimatedSprite extends Sprite {

    private final List<Map.Entry<Sprite, Double>> sprites;
    private int spriteIndex;

    public AnimatedSprite(List<Map.Entry<Sprite, Double>> sprites) {
        this(sprites, 0);
    }

    public AnimatedSprite(List<Map.Entry<Sprite, Double>> sprites, int firstSpriteIndex) {
        this.sprites = sprites;
        spriteIndex = firstSpriteIndex;

        GameEngineV2.getInstance().getSchedulerService().addTask(new AnimationTask(this), 1, 1);
    }

    @Override
    public Image get() {
        return this.sprites.get(this.spriteIndex).getKey().get();
    }

    private static class AnimationTask implements Task {

        private final AnimatedSprite animatedSprite;
        private double lastChangement;

        public AnimationTask(AnimatedSprite animatedSprite) {
            this.animatedSprite = animatedSprite;
            this.lastChangement = GameEngineV2.getInstance().getCurrentTime();
        }

        @Override
        public boolean onRun() {
            double duration = animatedSprite.sprites.get(animatedSprite.spriteIndex).getValue();
            if (GameEngineV2.getInstance().getCurrentTime() - this.lastChangement >= duration) {
                animatedSprite.spriteIndex = (this.animatedSprite.spriteIndex + 1) % this.animatedSprite.sprites.size();
                lastChangement = GameEngineV2.getInstance().getCurrentTime();
                return true;
            }

            return false;
        }

        @Override
        public void onStop() { }
    }
}
