package game.pkg_Player.pkg_Ui.pkg_Layer;

import game.pkg_Entity.Entity;
import game.pkg_Object.DrawType;
import game.pkg_Object.TileStateWithPos;
import game.pkg_Player.Player;
import game.pkg_Room.Room;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GameLayer extends JPanel {

    private final Player player;

    public GameLayer(Player player) {
        this.player = player;

        setLayout(new BorderLayout());
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        //Met le background en noir
        g2d.setBackground(Color.BLACK);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

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
            if (state.tile().getDrawType() == drawType) {
                state.tile().paint(g2d, state.position().vector2());
            }
        }
    }
}
