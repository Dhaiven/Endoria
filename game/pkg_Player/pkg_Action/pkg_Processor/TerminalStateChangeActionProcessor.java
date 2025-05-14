package game.pkg_Player.pkg_Action.pkg_Processor;

import game.pkg_Player.Player;
import game.pkg_Player.pkg_Action.Action;

public class TerminalStateChangeActionProcessor extends ActionProcessor {

    private boolean keyHasBeenReleased = true;

    public TerminalStateChangeActionProcessor() {
        super(Action.TERMINAL_STATE_CHANGE);
    }

    public void onKeyPressed(Player player) {
        if (!keyHasBeenReleased) return;
        keyHasBeenReleased = false;

        System.out.println("key has been released");
        player.getUserInterface().getTerminalInterface().setEnable(!player.getUserInterface().getTerminalInterface().isEnable());
    }

    public void onKeyReleased(Player player) {
        keyHasBeenReleased = true;
    }
}
