package game.pkg_Player.pkg_Action;

import game.pkg_Player.Player;

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
