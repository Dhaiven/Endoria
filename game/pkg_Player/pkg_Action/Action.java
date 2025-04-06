package game.pkg_Player.pkg_Action;

import game.GameState;

public enum Action {
    MOVE_FORWARD(GameState.PLAY),
    MOVE_BACKWARD(GameState.PLAY),
    MOVE_LEFT(GameState.PLAY),
    MOVE_RIGHT(GameState.PLAY),

    GAME_STATE_CHANGE(GameState.ALL, false),
    TERMINAL_STATE_CHANGE(GameState.ALL, false);

    private final GameState state;
    private final boolean canSpam;

    Action(GameState state) {
        this(state, true);
    }

    Action(GameState state, boolean canSpam) {
        this.state = state;
        this.canSpam = canSpam;
    }

    public GameState getState() {
        return state;
    }

    public boolean canSpam() {
        return canSpam;
    }
}
