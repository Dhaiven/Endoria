package game.image;

import game.entity.Entity;
import game.entity.FacingDirection;
import game.image.animation.AnimationManager;
import game.image.animation.action.MouvementAction;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class Animation {

    public static AnimatedSprite generateAnimation(BufferedImage image, int nbrOfImages, double duration) {
        List<Map.Entry<Sprite, Double>> animations = new ArrayList<>();
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        int step = width / nbrOfImages;
        for (int x = 0; x < width; x += step) {
            animations.add(new AbstractMap.SimpleEntry<>(
                    new StaticSprite(image.getSubimage(x, 0, step, height)),
                    duration
            ));
        }

        return new AnimatedSprite(animations);
    }

    public static AnimationManager test(Entity entity) {
        return new AnimationManager()
                .then(new MouvementAction(entity, FacingDirection.EAST, 70))
                .start();
    }
}
