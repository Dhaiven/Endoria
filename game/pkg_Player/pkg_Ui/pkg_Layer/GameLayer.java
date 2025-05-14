package game.pkg_Player.pkg_Ui.pkg_Layer;

import game.pkg_Entity.Entity;
import game.pkg_Object.DrawType;
import game.pkg_Object.TileStateWithPos;
import game.pkg_Object.Vector2;
import game.pkg_Player.Player;
import game.pkg_Room.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;

public class GameLayer extends JPanel {

    private final Player player;

    private BufferedImage graphics = null;
    private Rectangle bounds;
    private boolean boundsChanged = false;

    public GameLayer(Player player) {
        this.player = player;

        setLayout(new BorderLayout());
        setFocusable(true);
    }

    @Override
    public void repaint() {
        if (!boundsChanged) {
            bounds = null;
            boundsChanged = true;
        }

        super.repaint();
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        if (!boundsChanged) {
            bounds = new Rectangle(x, y, width, height);
            boundsChanged = true;
        }

        super.repaint(tm, x, y, width, height);
        bounds = null;
        boundsChanged = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        Room room = player.getPosition().room();

        Set<Integer> layers = room.getLayers();

        draw(g2d, room, 0, DrawType.UNDER);
        draw(g2d, room, 0, DrawType.ABOVE);

        for (int layer = 1; layer < layers.size(); layer++) {
            draw(g2d, room, layer, DrawType.UNDER);
        }
        for (int layer = 1; layer < layers.size(); layer++) {
            draw(g2d, room, layer, DrawType.ABOVE);
            for (Entity e : room.getEntities()) {
                if (e.getLayer() == layer) {
                    e.paint(g2d);
                }
            }
        }
    }

    private void draw(Graphics2D g2d, Room room, int layer, DrawType drawType) {
        for (TileStateWithPos state : room.getWorldsTilesCacheAtLayer(layer)) {
            if (bounds == null || bounds.contains(state.position().x(), state.position().y())) {
                if (state.tile().getDrawType() == drawType) {
                    state.tile().paint(g2d, state.position().vector2());
                }
            }
        }
    }
}
