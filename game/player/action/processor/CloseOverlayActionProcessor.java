package game.player.action.processor;

import game.player.Player;
import game.player.action.Action;

public class CloseOverlayActionProcessor extends ActionProcessor {

    private boolean keyHasBeenReleased = true;

    public CloseOverlayActionProcessor() {
        super(Action.CLOSE_OVERLAY);
    }

    public void onKeyPressed(Player player) {
        if (!keyHasBeenReleased) return;
        keyHasBeenReleased = false;

        player.getUserInterface().closeOpenedOverlay();
    }

    public void onKeyReleased(Player player) {
        keyHasBeenReleased = true;
    }
}