package game.player.action.processor;

import game.entity.FacingDirection;
import game.player.action.Action;

import java.util.HashMap;
import java.util.Map;

final public class ActionProcessorManager {

    private final Map<Action, ActionProcessor> actionProcessors = new HashMap<>();

    public ActionProcessorManager() {
        registerAction(new MovementActionProcessor(Action.MOVE_FORWARD, FacingDirection.NORTH));
        registerAction(new MovementActionProcessor(Action.MOVE_BACKWARD, FacingDirection.SOUTH));
        registerAction(new MovementActionProcessor(Action.MOVE_LEFT, FacingDirection.WEST));
        registerAction(new MovementActionProcessor(Action.MOVE_RIGHT, FacingDirection.EAST));

        registerAction(new OpenPauseOverlayActionProcessor());
        registerAction(new CloseOverlayActionProcessor());
    }

    public ActionProcessor getActionProcessor(final Action action) {
        return actionProcessors.get(action);
    }

    public void registerAction(ActionProcessor processor) {
        actionProcessors.put(processor.getAction(), processor);
    }
}
