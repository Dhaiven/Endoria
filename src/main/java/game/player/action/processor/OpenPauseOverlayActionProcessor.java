package game.player.action.processor;

import game.GameEngine;
import game.player.Player;
import game.player.action.Action;

public class OpenPauseOverlayActionProcessor extends ActionProcessor {

    private boolean keyHasBeenReleased = true;

    public OpenPauseOverlayActionProcessor() {
        super(Action.OPEN_PAUSE_OVERLAY);
    }

    public void onKeyPressed(Player player) {
        if (!keyHasBeenReleased) return;
        keyHasBeenReleased = false;

        player.getUserInterface().getPauseOverlay().setVisible(!GameEngine.getInstance().isPaused());
    }

    public void onKeyReleased(Player player) {
        keyHasBeenReleased = true;
    }
}