package game.pkg_Object;

import game.pkg_Image.StaticSprite;

import java.util.ArrayList;
import java.util.List;

public class ObjectCreator {

    public static Builder builder() {
        return new Builder();
    }

    private final int id;
    private final StaticSprite sprite;
    private final List<ObjectBehavior> objectBehaviors;

    private ObjectCreator(int id, StaticSprite sprite, List<ObjectBehavior> objectBehaviors) {
        this.id = id;
        this.sprite = sprite;
        this.objectBehaviors = objectBehaviors;
    }

    public static class Builder {
        private int id;
        private StaticSprite sprite;

        private final List<ObjectBehavior> objectBehaviors = new ArrayList<>();

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder sprite(StaticSprite sprite) {
            this.sprite = sprite;
            return this;
        }

        public Builder addObjectBehavior(ObjectBehavior objectBehavior) {
            objectBehaviors.add(objectBehavior);
            return this;
        }

        public ObjectCreator build() {
            return new ObjectCreator(id, sprite, objectBehaviors);
        }
    }
}
