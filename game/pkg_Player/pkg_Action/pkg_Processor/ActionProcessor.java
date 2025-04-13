package game.pkg_Player.pkg_Action.pkg_Processor;

import game.pkg_Player.Player;
import game.pkg_Player.pkg_Action.Action;

public abstract class ActionProcessor {

    private final Action action;

    public ActionProcessor(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public abstract void onKeyPressed(Player player);

    public abstract void onKeyReleased(Player player);
}
