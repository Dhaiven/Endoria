package game;

import game.pkg_Command.CommandManager;
import game.pkg_Image.Animation;
import game.pkg_Player.Player;
import game.pkg_Player.pkg_Ui.UserInterface;
import game.pkg_Scheduler.Scheduler;
import game.pkg_Util.FileUtils;
import game.pkg_World.WorldManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

public class GameEngineV2 implements Runnable {

    private static GameEngineV2 instance;

    private ScheduledFuture<?> future;

    private final Scheduler scheduler = new Scheduler();
    private final Player player;

    private long currentTime = 0;
    private long lastTime = 0;
    private double deltaTime;

    private long lastFps = 0;
    private long fps = 0;

    private boolean isPaused = false;

    private boolean forceUpdate = false;
    private Rectangle updateZone = null;

    public GameEngineV2() {
        instance = this;

        CommandManager commandManager = new CommandManager();

        try {
            var playerSprite = ImageIO.read(new File(FileUtils.ASSETS_RESOURCES + "test2.png"));
            var worldManager = new WorldManager();

            player = new Player(
                    player1 -> new UserInterface(player1, commandManager),
                    Animation.generateAnimation(playerSprite, 6, 100),
                    new Rectangle2D.Double(0, 93, 121, 24),
                    worldManager.getWorld("world1").getSpawnRoom()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GameEngineV2 getInstance() {
        return instance;
    }

    public Scheduler getSchedulerService() {
        return scheduler;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public GameState getGameState() {
        if (isPaused) {
            return GameState.PAUSE;
        }

        return GameState.PLAY;
    }

    public void start() {
        lastTime = System.currentTimeMillis();
        lastFps = System.currentTimeMillis();

        player.spawn();
        player.getUserInterface().repaint();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        future = scheduler.scheduleWithFixedDelay(this, 0, 1, TimeUnit.MILLISECONDS);
    }

    // Méthode principale de la boucle de jeu
    @Override
    public void run() {
        fps++;
        player.triggerKeys();

        boolean hasUpdate = forceUpdate;
        if (!isPaused) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - lastFps > 1000) {
                System.out.println("FPS: " + fps);
                lastFps = currentTimeMillis;
                fps = 0;
            }
            long deltaTime = currentTimeMillis - lastTime;

            this.currentTime += deltaTime;
            this.deltaTime = deltaTime / 1_000.0; // Conversion en secondes

            lastTime = currentTimeMillis;

            scheduler.onUpdate();
            hasUpdate = player.getPosition().room().onUpdate() || hasUpdate;
        }

        if (hasUpdate) {
            if (updateZone != null) {
                player.getUserInterface().repaint(
                        (int) updateZone.getX(),
                        (int) updateZone.getY(),
                        (int) updateZone.getWidth(),
                        (int) updateZone.getHeight()
                );
            } else {
                player.getUserInterface().repaint(
                        (int) player.getPosition().x() - 200,
                        (int) player.getPosition().y() - 200,
                        400, 400
                );
            }

            forceUpdate = false;
            updateZone = null;
        }
    }

    // Méthode pour arrêter complètement la boucle de jeu
    public void stop() {
        future.cancel(false);
        System.exit(0);
    }

    public void forceUpdate() {
        forceUpdate(null);
    }

    public void forceUpdate(Rectangle zone) {
        this.forceUpdate = true;
        this.updateZone = zone;
    }

    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Met le jeu en pose au prochain frmerate
     */
    public void pause() {
        isPaused = true;
    }

    /**
     * Reprend le jeu au prochain frame rate
     */
    public void resume() {
        isPaused = false;
        lastTime = System.currentTimeMillis();
    }
}