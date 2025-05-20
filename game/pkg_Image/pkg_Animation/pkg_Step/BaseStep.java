package game.pkg_Image.pkg_Animation.pkg_Step;

import game.pkg_Image.pkg_Animation.pkg_Action.AnimationAction;

public class BaseStep implements AnimationStep {
    private final AnimationAction[] actions;

    public BaseStep(AnimationAction[] actions) {
        this.actions = actions;
    }

    public void start() {
        System.out.println("startBaseStep");
        for (AnimationAction action : actions) {
            System.out.println("action");
            action.start();
            System.out.println("action2");
        }
        System.out.println("endBaseStep");
    }

    public void update(long elapsed) {
        System.out.println("update2");
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
