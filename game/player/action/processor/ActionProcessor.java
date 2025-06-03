package game.player.action.processor;

import game.player.Player;
import game.player.action.Action;

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
