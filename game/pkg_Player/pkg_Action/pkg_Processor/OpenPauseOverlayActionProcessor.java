package game.pkg_Player.pkg_Action.pkg_Processor;

import game.GameEngineV2;
import game.pkg_Player.Player;
import game.pkg_Player.pkg_Action.Action;

public class OpenPauseOverlayActionProcessor extends ActionProcessor {

    private boolean keyHasBeenReleased = true;

    public OpenPauseOverlayActionProcessor() {
        super(Action.OPEN_PAUSE_OVERLAY);
    }

    public void onKeyPressed(Player player) {
        if (!keyHasBeenReleased) return;
        keyHasBeenReleased = false;

        System.out.println("e");

        if (GameEngineV2.getInstance().isPaused()) {
            player.getUserInterface().getPauseOverlay().setVisible(false);
        } else {
            player.getUserInterface().getPauseOverlay().setVisible(true);
        }
    }

    public void onKeyReleased(Player player) {
        keyHasBeenReleased = true;
    }
}