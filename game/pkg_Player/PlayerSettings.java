package game.pkg_Player;

import game.GameState;
import game.pkg_Player.pkg_Action.Action;

import java.awt.event.KeyEvent;
import java.util.*;

public class PlayerSettings {

    private final Map<Action, List<Integer>> settings = new HashMap<>();

    public PlayerSettings() {
        // TODO: check si la config du joueur existe et si c le cas, load les commandes avec
        // sinon faire le code suivant

        for (Action action : Action.values()) {
            settings.put(action, new ArrayList<>());
        }

        register(Action.MOVE_FORWARD, KeyEvent.VK_Z, KeyEvent.VK_UP);
        register(Action.MOVE_BACKWARD, KeyEvent.VK_S, KeyEvent.VK_DOWN);
        register(Action.MOVE_LEFT, KeyEvent.VK_Q, KeyEvent.VK_LEFT);
        register(Action.MOVE_RIGHT, KeyEvent.VK_D, KeyEvent.VK_RIGHT);
        register(Action.GAME_STATE_CHANGE, KeyEvent.VK_ESCAPE);
        register(Action.TERMINAL_STATE_CHANGE, KeyEvent.VK_F2);
    }

    /**
     * @param keyCode {@link KeyEvent} CONSTANTS
     */
    public Action getActionFromKey(int keyCode, GameState state) {
        for (Map.Entry<Action, List<Integer>> entry : settings.entrySet()) {
            if (entry.getKey().getState() != state) continue;

            for (Integer value : entry.getValue()) {
                if (value == keyCode) {
                    return entry.getKey();
                }
            }
        }


        return null;
    }

    /**
     * @param keysCode {@link KeyEvent} CONSTANTS
     */
    private void register(Action action, Integer ...keysCode) {
        settings.put(action, Arrays.asList(keysCode));
    }


}
