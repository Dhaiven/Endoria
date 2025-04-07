package game.pkg_Scheduler;

public interface Task {
    /**
     * @return true if we need to repaint else false
     */
    boolean onRun();

    void onStop();
}
