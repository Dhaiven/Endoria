package game.scheduler;

public final class RunningTaskInfo {
    private final Task task;
    private final int delay;
    private final int period;

    private long nextRunTick;

    private boolean isRunning = false;
    private boolean isCancelled = false;

    public RunningTaskInfo(Task task, int delay, int period) {
        this.task = task;
        this.delay = delay;
        this.period = period;
    }

    public Task getTask() {
        return task;
    }

    public int getDelay() {
        return delay;
    }

    public int getPeriod() {
        return period;
    }

    public long getNextRunTick() {
        return nextRunTick;
    }

    public boolean isRepeating() {
        return period > 0;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void start() {
        start(false);
    }

    public void start(boolean isRepeating) {
        isCancelled = false;
        isRunning = true;
        nextRunTick = System.nanoTime() + (isRepeating ? delay : period);
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void cancel() {
        isRunning = false;
        isCancelled = true;
    }
}