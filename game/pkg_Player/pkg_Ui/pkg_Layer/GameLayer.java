package game.pkg_Player.pkg_Ui.pkg_Layer;

import game.pkg_Entity.Entity;
import game.pkg_Object.DrawType;
import game.pkg_Object.TileStateWithPos;
import game.pkg_Player.Player;
import game.pkg_Room.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Set;

public class GameLayer extends Canvas {

    private final Player player;

    private Rectangle bounds = null;

    private BufferStrategy bufferStrategy;

    private BufferedImage cacheImage;
    private BufferedImage patchImage;

    public GameLayer(Player player) {
        this.player = player;

        setFocusable(true);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (getWidth() == 0 || getHeight() == 0) return;
                cacheImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                patchImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            }

            @Override
            public void componentMoved(ComponentEvent e) {}

            @Override
            public void componentShown(ComponentEvent e) {}

            @Override
            public void componentHidden(ComponentEvent e) {}
        });

        cacheImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        patchImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        System.out.println("addNotify");
        createBufferStrategy(3);
        bufferStrategy = getBufferStrategy();

        render();
    }

    @Override
    public void repaint() {
        bounds = null;

        if (bufferStrategy != null) {
            render();
        }
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        bounds = new Rectangle(x, y, width, height);

        render();
    }

    private void render() {
        do {
            if (bounds != null) {
                Graphics2D patchGraphics = patchImage.createGraphics();
                patchGraphics.translate(-bounds.x, -bounds.y);
                render(patchGraphics);
                patchGraphics.dispose();

                Graphics2D cacheGraphics = cacheImage.createGraphics();
                cacheGraphics.drawImage(patchImage, bounds.x, bounds.y, null);
                cacheGraphics.dispose();

                bounds = null;
            } else {
                Graphics2D cacheGraphics = cacheImage.createGraphics();
                render(cacheGraphics);
                cacheGraphics.dispose();
            }

            Graphics g = bufferStrategy.getDrawGraphics();
            g.drawImage(cacheImage, 0, 0, null);
            g.dispose();
        } while (bufferStrategy.contentsRestored());

        bufferStrategy.show();
    }

    private void render(Graphics2D g2d) {
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
            if (state.tile().getDrawType() != drawType) continue;

            state.tile().paint(g2d, state.position().vector2());
        }
    }
}


