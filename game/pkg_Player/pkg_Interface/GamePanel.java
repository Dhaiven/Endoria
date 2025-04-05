package game.pkg_Player.pkg_Interface;

import game.pkg_Entity.Entity;
import game.pkg_Object.Vector2;
import game.pkg_Player.Player;
import game.pkg_Room.Room;
import game.pkg_Tile.Tile;
import game.pkg_World.World;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GamePanel extends JPanel {

    private final Player player;

    public GamePanel(Player player) {
        this.player = player;

        setLayout(null);
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
        for (int layer : World.LAYERS) {
            for (Map.Entry<Vector2, Tile> entry : room.getWorldsTilesCacheAtLayer(layer).entrySet()) {
                entry.getValue().paint(g2d, entry.getKey());
            }

            for (Entity entity : room.getEntities()) {
                if (entity.getLayer() == layer) {
                    entity.paint(g2d);
                }
            }
        }
    }
}
