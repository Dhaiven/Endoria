package game.pkg_Player.pkg_Action.pkg_Processor;

import game.pkg_Entity.FacingDirection;
import game.pkg_Player.pkg_Action.Action;

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

        registerAction(new TerminalStateChangeActionProcessor());
    }

    public ActionProcessor getActionProcessor(final Action action) {
        return actionProcessors.get(action);
    }

    public void registerAction(ActionProcessor processor) {
        actionProcessors.put(processor.getAction(), processor);
    }
}
