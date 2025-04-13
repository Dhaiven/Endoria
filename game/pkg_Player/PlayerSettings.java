package game.pkg_Player;

import game.GameState;
import game.pkg_Player.pkg_Action.Action;

import java.awt.event.KeyEvent;
import java.util.*;

public class PlayerSettings {

    private final Map<Action, Integer[]> settings = new HashMap<>();

    public PlayerSettings() {
        // TODO: check si la config du joueur existe et si c le cas, load les commandes avec
        // sinon faire le code suivant

        for (Action action : Action.values()) {
            settings.put(action, new Integer[2]);
        }

        register(Action.MOVE_FORWARD, KeyEvent.VK_Z, KeyEvent.VK_UP);
        register(Action.MOVE_BACKWARD, KeyEvent.VK_S, KeyEvent.VK_DOWN);
        register(Action.MOVE_LEFT, KeyEvent.VK_Q, KeyEvent.VK_LEFT);
        register(Action.MOVE_RIGHT, KeyEvent.VK_D, KeyEvent.VK_RIGHT);

        register(Action.OPEN_PAUSE_OVERLAY, KeyEvent.VK_ESCAPE);
        register(Action.CLOSE_OVERLAY, KeyEvent.VK_ESCAPE);

        register(Action.TERMINAL_STATE_CHANGE, KeyEvent.VK_F2);
    }

    /**
     * @param keyCode {@link KeyEvent} CONSTANTS
     */
    public Action getActionFromKey(int keyCode, GameState state) {
        for (Map.Entry<Action, Integer[]> entry : settings.entrySet()) {
            if (entry.getKey().getState() != state) continue;

            for (Integer value : entry.getValue()) {
                if (value != null && value == keyCode) {
                    return entry.getKey();
                }
            }
        }


        return null;
    }

    /**
     * @return a int tab with 3 elements {@link KeyEvent} CONSTANTS
     */
    public Integer[] getKeyFromAction(Action action) {
       return settings.get(action);
    }

    public void replaceKeyFromAction(Action action, int index, Integer newKeyCode) {
        Integer[] keysCode = getKeyFromAction(action);
        keysCode[index] = newKeyCode;
    }

    /**
     * @param keyCode {@link KeyEvent} CONSTANTS
     */
    private void register(Action action, Integer keyCode) {
        Integer[] keysCode = settings.get(action);
        keysCode[0] = keyCode;
    }

    /**
     * @param firstKeyCode {@link KeyEvent} CONSTANTS
     * @param secondKeyCode {@link KeyEvent} CONSTANTS
     */
    private void register(Action action, int firstKeyCode, int secondKeyCode) {
        Integer[] keysCode = settings.get(action);
        keysCode[0] = firstKeyCode;
        keysCode[1] = secondKeyCode;
    }
}
