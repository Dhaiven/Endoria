package game.player;

import game.GameEngine;
import game.player.action.Action;

import java.awt.event.*;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class PlayerEventListener implements KeyListener, WindowFocusListener {

    private final Player player;
    private boolean enable = true;

    private final Set<Action> keysPressed = new ConcurrentSkipListSet<>();
    private final Set<Action> keysReleased = new ConcurrentSkipListSet<>();

    public PlayerEventListener(final Player player) {
        this.player = player;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!enable) return;

        Action action = player.getSettings().getActionFromKey(e.getKeyCode(), GameEngine.getInstance().getGameState());
        if (action != null) {
            keysPressed.add(action);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!enable) return;

        Action action = player.getSettings().getActionFromKey(e.getKeyCode(), GameEngine.getInstance().getGameState());
        if (action != null) {
            keysReleased.add(action);
            keysPressed.remove(action);
        }
    }

    public Set<Action> getKeysPressed() {
        return keysPressed;
    }

    public Set<Action> getKeysReleased() {
        return keysReleased;
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        enable = true;
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        // Quand on quitte la fenetre, on supprime tous les actions en cours
        keysPressed.clear();
        keysReleased.clear();
        enable = false;
    }
}
