package game.pkg_Image.pkg_Animation.pkg_Action;

import game.GameEngineV2;
import game.pkg_Entity.Entity;
import game.pkg_Entity.FacingDirection;
import game.pkg_Object.Vector2;

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
        lasUpdateTime = GameEngineV2.getInstance().getCurrentTime();
    }

    @Override
    public void update(long elapsedTime) {
        if (GameEngineV2.getInstance().getCurrentTime() >= lasUpdateTime) {
            this.entity.move(this.direction, 1);
            lasUpdateTime = GameEngineV2.getInstance().getCurrentTime() + 1;
        }
    }

    @Override
    public boolean isFinished() {
        double startPosition = this.direction.getVectorComponent(this.startPosition);
        double actualPositon = this.direction.getVectorComponent(this.entity.getPosition().vector2());
        return Math.abs(actualPositon - startPosition) >= this.amount;
    }
}
