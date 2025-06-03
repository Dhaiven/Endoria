package game.image.animation.action;

import game.GameEngine;
import game.entity.Entity;
import game.entity.FacingDirection;
import game.object.Vector2;

public class MouvementAction implements AnimationAction {

    private final Entity entity;
    private final FacingDirection direction;
    private final int amount;

    private long lasUpdateTime;

    private Vector2 startPosition;

    public MouvementAction(Entity entity, FacingDirection direction, int amount) {
        this.entity = entity;
        this.direction = direction;
        this.amount = amount;
    }

    @Override
    public void start() {
        this.startPosition = entity.getPosition().vector2();
        lasUpdateTime = GameEngine.getInstance().getCurrentTime();
    }

    @Override
    public void update(long elapsedTime) {
        if (GameEngine.getInstance().getCurrentTime() >= lasUpdateTime) {
            this.entity.move(this.direction, 1);
            lasUpdateTime = GameEngine.getInstance().getCurrentTime() + 1;
        }
    }

    @Override
    public boolean isFinished() {
        double startPosition = this.direction.getVectorComponent(this.startPosition);
        double actualPositon = this.direction.getVectorComponent(this.entity.getPosition().vector2());
        return Math.abs(actualPositon - startPosition) >= this.amount;
    }
}
