package game.pkg_Entity.pkg_Player;

import game.pkg_Entity.FacingDirection;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PlayerInput implements KeyListener {
    private final Player player;
    private boolean enable = true;

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

        switch (e.getKeyCode()) {
            case KeyEvent.VK_Z -> this.player.move(FacingDirection.NORTH);
            case KeyEvent.VK_S -> this.player.move(FacingDirection.SOUTH);
            case KeyEvent.VK_D -> this.player.move(FacingDirection.EAST);
            case KeyEvent.VK_Q -> this.player.move(FacingDirection.WEST);
            case KeyEvent.VK_F2 -> this.player.getUserInterface().enable(false);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
