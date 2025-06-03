package game.scheduler;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class Scheduler {

    protected static final Comparator<RunningTaskInfo> COMPARATOR = Comparator.comparing(RunningTaskInfo::getNextRunTick);

    protected final PriorityBlockingQueue<RunningTaskInfo> queue = new PriorityBlockingQueue<>(10, COMPARATOR);

    private final AnimationTask animationTask = new AnimationTask();

    public Scheduler() {
        addTask(animationTask, 1, 10);
    }

    public AnimationTask getAnimationTask() {
        return animationTask;
    }

    public void onUpdate() {
        while (!queue.isEmpty() && queue.peek().getNextRunTick() <= System.nanoTime()) {
            RunningTaskInfo taskInfo = queue.poll();
            if (taskInfo == null || !taskInfo.isRunning()) {
                continue;
            }

            runTask(taskInfo);
        }
    }

    public void addTask(Task task, int delay) {
        this.addTask(task, delay, 0);
    }

    public RunningTaskInfo addTask(Task task, int delay, int period) {
        RunningTaskInfo taskInfo = new RunningTaskInfo(task, delay, period);
        taskInfo.start();

        this.addTask(taskInfo);

        return taskInfo;
    }

    public boolean addTask(RunningTaskInfo taskInfo) {
        return this.queue.add(taskInfo);
    }

    protected void runTask(RunningTaskInfo info) {
        try {
            info.getTask().onRun();
        } catch (Exception exception) {
            cancelTask(info);
        } finally {
            if (!info.isCancelled() && info.isRepeating()) {
                info.start(true);
                addTask(info);
            } else {
                cancelTask(info);
            }
        }
    }

    protected void cancelTask(RunningTaskInfo info) {
        if (info.isCancelled()) {
            return;
        }

        info.cancel();
        info.getTask().onStop();
    }
}
