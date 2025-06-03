package game.image.animation.step;

public interface AnimationStep {

    void start();
    void update(long elapsed);
    boolean isFinished(long elapsed);
}
