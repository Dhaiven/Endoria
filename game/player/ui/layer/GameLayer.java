package game.player.ui.layer;

import game.entity.Entity;
import game.player.Player;
import game.util.Utils;
import game.world.World;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class GameLayer extends Canvas {

    private final Player player;

    private Rectangle bounds = null;
    private Rectangle nextBounds = null;

    private BufferStrategy bufferStrategy;

    private BufferedImage cacheImage;
    private BufferedImage patchImage;

    private boolean inRender = false;

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

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (isFocusOwner()) {
                // Redirige vers ton gestionnaire
                switch (e.getID()) {
                    case KeyEvent.KEY_PRESSED -> player.getEventManager().keyPressed(e);
                    case KeyEvent.KEY_RELEASED -> player.getEventManager().keyReleased(e);
                    case KeyEvent.KEY_TYPED -> player.getEventManager().keyTyped(e);
                }
                return true; // Événement consommé
            }
            return false;
        });

        cacheImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        patchImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        createBufferStrategy(3);
        bufferStrategy = getBufferStrategy();
        System.out.println("bufferStrategy = " + bufferStrategy);
        repaint();
    }

    @Override
    public void repaint() {
        System.out.println("repaint          e");
        if (inRender) {
            System.out.println("repaint in render");
            return;
        }
        bounds = null;

        System.out.println("bounds null in repaint()");
        if (bufferStrategy != null) {
            System.out.println("bufferStrategy not null");
            render();
        }
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        if (inRender) {
            int tileW = Utils.TEXTURE_SIZE.x();
            int tileH = Utils.TEXTURE_SIZE.y();

            int startX = (int) Math.floor((double) x / tileW) * tileW;
            int startY = (int) Math.floor((double) y / tileH) * tileH;
            int endX   = (int) Math.ceil((x + width) / (double) tileW) * tileW;
            int endY   = (int) Math.ceil((y + height) / (double) tileH) * tileH;

            var nextBounds2 = new Rectangle(startX, startY, endX - startX, endY - startY);
            if (nextBounds != null) {
                nextBounds = nextBounds.union(nextBounds2);
            } else {
                nextBounds = nextBounds2;
            }
            return;
        } else if (x == width && y == height) {
            return;
        }
        int tileW = Utils.TEXTURE_SIZE.x();
        int tileH = Utils.TEXTURE_SIZE.y();

        int startX = (int) Math.floor((double) x / tileW) * tileW;
        int startY = (int) Math.floor((double) y / tileH) * tileH;
        int endX   = (int) Math.ceil((x + width) / (double) tileW) * tileW;
        int endY   = (int) Math.ceil((y + height) / (double) tileH) * tileH;

        bounds = new Rectangle(startX, startY, endX - startX, endY - startY);
        if (nextBounds != null) {
            bounds = bounds.union(nextBounds);
        }
        nextBounds = null;

        render();
    }

    private void render() {
        inRender = true;
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
        inRender = false;
    }

    private void render(Graphics2D g2d) {
        World world = player.getPosition().world();

        g2d.setColor(Color.RED);
        for (var tileSet : world.getTilesInView(null)) {
            for (var state : tileSet) {
                state.tile().paint(g2d, state.cell().toPixel());
            }
        }

        for (Entity entity : world.getEntities()) {
            entity.paint(g2d);
        }
    }
}


