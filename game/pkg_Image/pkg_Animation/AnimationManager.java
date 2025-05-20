package game.pkg_Image.pkg_Animation;

import game.GameEngineV2;
import game.pkg_Image.pkg_Animation.pkg_Action.AnimationAction;
import game.pkg_Image.pkg_Animation.pkg_Step.*;
import game.pkg_Scheduler.Task;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public class AnimationManager {
    private final Queue<AnimationStep> steps = new LinkedList<>();
    private AnimationStep currentStep;
    private long stepStartTime;

    public AnimationManager then(long duration, AnimationAction... actions) {
        steps.add(new TimedStep(duration, actions));
        return this;
    }

    public AnimationManager then(AnimationAction... actions) {
        steps.add(new BaseStep(actions));
        return this;
    }

    public AnimationManager waitUntil(Supplier<Boolean> condition) {
        steps.add(new WaitStep(condition));
        return this;
    }

    public AnimationManager start() {
        //GameEngineV2.getInstance().getSchedulerService().addTask(new AnimationTask(this), 100000, 100000);
        return this;
    }

    public void update(long currentTime) {
        if (currentStep == null && !steps.isEmpty()) {
            currentStep = steps.poll();
            stepStartTime = currentTime;
            currentStep.start();
        }

        if (currentStep != null) {
            currentStep.update(currentTime - stepStartTime);

            if (currentStep.isFinished(currentTime - stepStartTime)) {
                currentStep = null;
                GameEngineV2.getInstance().forceUpdate();
            }
        }
    }

    private static class AnimationTask implements Task {

        private final AnimationManager manager;

        public AnimationTask(AnimationManager manager) {
            this.manager = manager;
        }

        @Override
        public void onRun() {
            this.manager.update(GameEngineV2.getInstance().getCurrentTime());
        }

        @Override
        public void onStop() {

        }
    }
}
