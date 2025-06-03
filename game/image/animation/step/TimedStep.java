package game.image.animation.step;

import game.image.animation.action.AnimationAction;

public class TimedStep implements AnimationStep {
    private final long duration;
    private final AnimationAction[] actions;

    public TimedStep(long duration, AnimationAction[] actions) {
        this.duration = duration;
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
        return elapsed >= duration;
    }
}
