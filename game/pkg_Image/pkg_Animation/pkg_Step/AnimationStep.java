package game.pkg_Image.pkg_Animation.pkg_Step;

public interface AnimationStep {

    void start();
    void update(long elapsed);
    boolean isFinished(long elapsed);
}
