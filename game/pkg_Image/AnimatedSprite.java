package game.pkg_Image;

import game.GameEngineV2;
import game.pkg_Scheduler.Task;

import java.awt.Image;
import java.util.List;
import java.util.Map;

public class AnimatedSprite extends Sprite {

    private final List<Map.Entry<Sprite, Double>> sprites;
    private int spriteIndex;

    private final AnimationTask task;

    public AnimatedSprite(List<Map.Entry<Sprite, Double>> sprites) {
        this(sprites, 0);
    }

    public AnimatedSprite(List<Map.Entry<Sprite, Double>> sprites, int firstSpriteIndex) {
        this(sprites, firstSpriteIndex, null);
    }

    public AnimatedSprite(List<Map.Entry<Sprite, Double>> sprites, int firstSpriteIndex, AnimatedSpriteObserver observer) {
        this.sprites = sprites;
        this.spriteIndex = firstSpriteIndex;

        this.task = new AnimationTask(this, observer);
        //TODO
        //GameEngineV2.getInstance().getSchedulerService().addTask(this.task, 1, 1);
    }

    @Override
    public Image get() {
        return this.sprites.get(this.spriteIndex).getKey().get();
    }

    public void setObserver(AnimatedSpriteObserver observer) {
        this.task.setObserver(observer);
    }

    private static class AnimationTask implements Task {

        private final AnimatedSprite animatedSprite;
        private AnimatedSpriteObserver observer;
        private double lastChangement;

        public AnimationTask(AnimatedSprite animatedSprite, AnimatedSpriteObserver observer) {
            this.animatedSprite = animatedSprite;
            this.observer = observer;
            this.lastChangement = GameEngineV2.getInstance().getCurrentTime();
        }

        public void setObserver(AnimatedSpriteObserver observer) {
            this.observer = observer;
        }

        @Override
        public void onRun() {
            double duration = animatedSprite.sprites.get(animatedSprite.spriteIndex).getValue();
            if (GameEngineV2.getInstance().getCurrentTime() - this.lastChangement >= duration) {
                Image oldImage = this.animatedSprite.get();
                animatedSprite.spriteIndex = (this.animatedSprite.spriteIndex + 1) % this.animatedSprite.sprites.size();
                lastChangement = GameEngineV2.getInstance().getCurrentTime();

                if (observer != null) {
                    observer.imageChanged(oldImage, this.animatedSprite.get());
                }
                GameEngineV2.getInstance().forceUpdate();
            }
        }

        @Override
        public void onStop() { }
    }
}
