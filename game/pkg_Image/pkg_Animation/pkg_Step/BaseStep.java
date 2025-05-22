package game.pkg_Image.pkg_Animation.pkg_Step;

import game.pkg_Image.pkg_Animation.pkg_Action.AnimationAction;

public class BaseStep implements AnimationStep {
    private final AnimationAction[] actions;

    public BaseStep(AnimationAction[] actions) {
        this.actions = actions;
    }

    public void start() {
        for (AnimationAction action : actions) {
            action.start();
        }
    }

    public void update(long elapsed) {
        for (AnimationAction action : actions) {
            action.update(elapsed);
        }
    }

    public boolean isFinished(long elapsed) {
        for (AnimationAction action : actions) {
            if (!action.isFinished()) {
                return false;
            }
        }

        return true;
    }
}
