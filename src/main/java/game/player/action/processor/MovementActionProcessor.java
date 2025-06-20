package game.player.action.processor;

import game.entity.FacingDirection;
import game.player.Player;
import game.player.action.Action;

public class MovementActionProcessor extends ActionProcessor {

    private final FacingDirection direction;

    public MovementActionProcessor(Action action, FacingDirection direction) {
        super(action);
        this.direction = direction;
    }

    public void onKeyPressed(Player player) {
        //player.move(direction);
    }

    public void onKeyReleased(Player player) {
    }
}