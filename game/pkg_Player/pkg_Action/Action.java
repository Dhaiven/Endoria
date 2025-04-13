package game.pkg_Player.pkg_Action;

import game.GameState;

public enum Action {
    MOVE_FORWARD("UP", GameState.PLAY),
    MOVE_BACKWARD("DOWN", GameState.PLAY),
    MOVE_LEFT("LEFT", GameState.PLAY),
    MOVE_RIGHT("RIGHT", GameState.PLAY),

    GAME_STATE_CHANGE("Pause/Reprendre", GameState.ALL, false),
    TERMINAL_STATE_CHANGE("Afficher/Cacher le terminal", GameState.ALL, false),

    OPEN_PAUSE_OVERLAY("Mettre le jeu en pause", GameState.PLAY, false),
    CLOSE_OVERLAY("Ferme l'interface", GameState.PAUSE, false);

    private final String name;
    private final GameState state;
    private final boolean canSpam;

    Action(String name, GameState state) {
        this(name, state, true);
    }

    Action(String name, GameState state, boolean canSpam) {
        this.name = name;
        this.state = state;
        this.canSpam = canSpam;
    }

    public String getName() {
        return name;
    }

    public GameState getState() {
        return state;
    }

    public boolean canSpam() {
        return canSpam;
    }
}
