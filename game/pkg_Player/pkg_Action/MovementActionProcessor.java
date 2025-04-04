package game.pkg_Player.pkg_Action;

import game.GameEngineV2;
import game.pkg_Entity.FacingDirection;
import game.pkg_Player.Player;

import java.awt.event.KeyEvent;

public class MovementActionProcessor extends ActionProcessor {

    private final FacingDirection direction;

    public MovementActionProcessor(Action action, FacingDirection direction) {
        super(action);
        this.direction = direction;
    }

    public void onKeyPressed(Player player) {
        player.move(direction);
    }

    public void onKeyReleased(Player player) {
    }
}