package game.image.animation.action;

public interface AnimationAction {

    void start();
    void update(long elapsedTime);
    boolean isFinished();

}
