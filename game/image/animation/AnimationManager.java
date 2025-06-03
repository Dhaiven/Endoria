package game.image.animation;

import game.GameEngine;
import game.image.animation.action.AnimationAction;
import game.image.animation.step.*;
import game.scheduler.Task;

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
                GameEngine.getInstance().forceUpdate();
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
            this.manager.update(GameEngine.getInstance().getCurrentTime());
        }

        @Override
        public void onStop() {

        }
    }
}
