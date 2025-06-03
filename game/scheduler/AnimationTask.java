package game.scheduler;

import game.GameEngine;
import game.image.AnimatedSprite;
import game.world.World;

import java.util.ArrayList;
import java.util.List;

public class AnimationTask implements Task {

    private final List<Info> nextSpritesUpdated = new ArrayList<>();

    public void addSprite(World world, AnimatedSprite animatedSprite) {
        var info = new Info(world, animatedSprite, 0, GameEngine.getInstance().getCurrentTime());
        nextSpritesUpdated.add(info);
    }

    @Override
    public void onRun() {
        var currentTime = GameEngine.getInstance().getCurrentTime();

        var spritesMustBeUpdated = nextSpritesUpdated.stream()
                .filter(info -> info.mustBeUpdated(currentTime));
        spritesMustBeUpdated.toList().forEach(info -> {
            var newInfo = info.next(currentTime);
            System.out.println("update");
            if (info.sprite().getObserver() != null) {
                System.out.println("(info.sprite().getObserver()");
                info.sprite().getObserver().imageChanged(info.sprite().get(), newInfo.sprite.get());
            }

            nextSpritesUpdated.remove(info);
            nextSpritesUpdated.add(newInfo);
        });
    }

    @Override
    public void onStop() { }

    private record Info(World world, AnimatedSprite sprite, int index, double startAt) {

        public double duration() {
            return sprite.getSprites().get(index).getValue();
        }

        public boolean mustBeUpdated(double currentTime) {
            return world.isLoaded() && startAt + duration() <  currentTime;
        }

        public Info next(double actualTime) {
            var allSprites = sprite.getSprites();
            int length = allSprites.size();

            var entry = allSprites.get(index);
            int spriteIndex = index;
            double start = startAt;
            while (entry.getValue() < (actualTime - start)) {
                spriteIndex = (spriteIndex + 1) % length;
                start += entry.getValue();
                entry = allSprites.get(spriteIndex);
            }

            sprite.setSpriteIndex(spriteIndex);
            return new Info(world, sprite, spriteIndex, start);
        }
    }
}
