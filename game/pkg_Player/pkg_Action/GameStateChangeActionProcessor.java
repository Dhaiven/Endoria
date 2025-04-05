package game.pkg_Player.pkg_Action;

import game.GameEngineV2;
import game.pkg_Player.Player;

public class GameStateChangeActionProcessor extends ActionProcessor {

    private boolean keyHasBeenReleased = true;

    public GameStateChangeActionProcessor() {
        super(Action.GAME_STATE_CHANGE);
    }

    public void onKeyPressed(Player player) {
        if (!keyHasBeenReleased) return;
        keyHasBeenReleased = false;

        if (GameEngineV2.getInstance().isPaused()) {
            GameEngineV2.getInstance().resume();
        } else {
            GameEngineV2.getInstance().pause();
        }
    }

    public void onKeyReleased(Player player) {
        keyHasBeenReleased = true;
    }
}
