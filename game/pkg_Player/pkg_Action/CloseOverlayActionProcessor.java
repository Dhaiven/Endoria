package game.pkg_Player.pkg_Action;

import game.pkg_Player.Player;

public class CloseOverlayActionProcessor extends ActionProcessor {

    private boolean keyHasBeenReleased = true;

    public CloseOverlayActionProcessor() {
        super(Action.CLOSE_OVERLAY);
    }

    public void onKeyPressed(Player player) {
        if (!keyHasBeenReleased) return;
        keyHasBeenReleased = false;

        System.out.println("a");

        player.getUserInterface().closeOpenedOverlay();
    }

    public void onKeyReleased(Player player) {
        keyHasBeenReleased = true;
    }
}