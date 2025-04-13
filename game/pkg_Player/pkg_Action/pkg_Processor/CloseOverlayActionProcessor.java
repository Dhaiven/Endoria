package game.pkg_Player.pkg_Action.pkg_Processor;

import game.pkg_Player.Player;
import game.pkg_Player.pkg_Action.Action;

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