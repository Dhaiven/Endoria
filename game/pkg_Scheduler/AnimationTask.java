package game.pkg_Scheduler;

import game.GameEngineV2;
import game.pkg_Image.AnimatedSprite;
import game.pkg_Room.Room;

import java.util.ArrayList;
import java.util.List;

public class AnimationTask implements Task {

    private final List<Info> nextSpritesUpdated = new ArrayList<>();

    public void addSprite(Room room, AnimatedSprite animatedSprite) {
        var info = new Info(room, animatedSprite, 0, GameEngineV2.getInstance().getCurrentTime());
        nextSpritesUpdated.add(info);
    }

    @Override
    public void onRun() {
        var currentTime = GameEngineV2.getInstance().getCurrentTime();

        var spritesMustBeUpdated = nextSpritesUpdated.stream()
                .filter(info -> info.mustBeUpdated(currentTime));
        spritesMustBeUpdated.toList().forEach(info -> {
            var newInfo = info.next(currentTime);
            if (info.sprite().getObserver() != null) {
                info.sprite().getObserver().imageChanged(info.sprite().get(), newInfo.sprite.get());
            }

            nextSpritesUpdated.remove(info);
            nextSpritesUpdated.add(newInfo);
        });
    }

    @Override
    public void onStop() { }

    private record Info(Room room, AnimatedSprite sprite, int index, double startAt) {

        public double duration() {
            return sprite.getSprites().get(index).getValue();
        }

        public boolean mustBeUpdated(double currentTime) {
            return room.isLoaded() && startAt + duration() <  currentTime;
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
            return new Info(room, sprite, spriteIndex, start);
        }
    }
}
