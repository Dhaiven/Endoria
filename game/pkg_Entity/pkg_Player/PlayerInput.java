package game.pkg_Entity.pkg_Player;

import game.GameEngineV2;
import game.pkg_Entity.FacingDirection;

import java.awt.event.*;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerInput implements KeyListener, WindowFocusListener {
    private final Player player;
    private boolean enable = true;

    /**
     * Thread safe
     */
    private final Set<FacingDirection> movements = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public PlayerInput(final Player player) {
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

        FacingDirection movement = findMoveDirection(e);
        if (movement != null) {
            movements.add(movement);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        FacingDirection movement = findMoveDirection(e);
        if (movement != null) {
            movements.remove(movement);
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (GameEngineV2.getInstance().isPaused()) {
                player.getUserInterface().resume();
            } else {
                player.getUserInterface().pause();
            }
        }
    }

    private FacingDirection findMoveDirection(KeyEvent e) {
        return switch (e.getKeyCode()) {
            case KeyEvent.VK_Z -> FacingDirection.NORTH;
            case KeyEvent.VK_S -> FacingDirection.SOUTH;
            case KeyEvent.VK_D -> FacingDirection.EAST;
            case KeyEvent.VK_Q -> FacingDirection.WEST;
            default -> null;
        };
    }

    public Set<FacingDirection> getMovements() {
        return movements;
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {

    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        // Quand on quitte la fenetre, on supprime tous les mouvements en cours
        movements.clear();
    }
}
