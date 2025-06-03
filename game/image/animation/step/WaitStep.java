package game.image.animation.step;

import java.util.function.Supplier;

public class WaitStep implements AnimationStep {
    private final Supplier<Boolean> condition;

    public WaitStep(Supplier<Boolean> condition) {
        this.condition = condition;
    }

    public void start() {}

    public void update(long elapsed) {}

    public boolean isFinished(long elapsed) {
        return condition.get();
    }
}
