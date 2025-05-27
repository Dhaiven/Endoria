package game;

import game.pkg_Command.CommandManager;
import game.pkg_Image.StaticSprite;
import game.pkg_Player.Player;
import game.pkg_Player.pkg_Ui.UserInterface;
import game.pkg_Scheduler.Scheduler;
import game.pkg_Tile.TileManager;
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

    private final CommandManager commandManager = new CommandManager();
    private final Scheduler scheduler = new Scheduler();
    private final TileManager tileManager = new TileManager();
    private final Player player;

    private long start = 0;
    private long currentTime = 0;
    private long lastTime = 0;
    private double deltaTime;

    private int fps = 0;

    private boolean isPaused = false;

    private boolean forceUpdate = false;
    private Rectangle updateZone = null;

    private String alea = null;

    public GameEngineV2() {
        instance = this;

        try {
            var playerSprite = ImageIO.read(new File(FileUtils.ASSETS_RESOURCES + "player/idle/playerIdle2.png"));
            var worldManager = new WorldManager();

            player = new Player(
                    player1 -> new UserInterface(player1, commandManager),
                    new StaticSprite(playerSprite),
                    new Rectangle2D.Double(0, 38, 48, 10),
                    worldManager.getWorld("forestWorld")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GameEngineV2 getInstance() {
        return instance;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Scheduler getSchedulerService() {
        return scheduler;
    }

    public TileManager getTileManager() {
        return tileManager;
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

    public Player getPlayer() {
        return player;
    }

    public String getAlea() {
        return alea;
    }

    public void setAlea(String alea) {
        this.alea = alea;
    }

    public void start() {
        lastTime = System.currentTimeMillis();
        start = lastTime;

        player.spawn();
        player.getUserInterface().getGameLayer().repaint();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        future = scheduler.scheduleWithFixedDelay(this, 0, 1, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            long endTime = System.currentTimeMillis();
            double elapsedSeconds = (endTime - start) / 1000.0;
            if (elapsedSeconds > 0) {
                double averageFPS = fps / elapsedSeconds;
                System.out.println("FPS moyen: " + averageFPS);
            } else {
                System.out.println("Durée trop courte pour calculer les FPS moyens.");
            }
        }));

        player.getUserInterface().println("Bienvenue dans le monde de Endoria");
        player.getUserInterface().println("Votre objectif: trouver le diamand secrét de musée !");
        player.getUserInterface().println("Besoin d'aide ? Taper help");
        player.getUserInterface().println("");
        player.getUserInterface().println("Bonne chance !!!");
        player.getUserInterface().println("");
        player.getUserInterface().printLocationInfo();
    }

    // Méthode principale de la boucle de jeu
    @Override
    public void run() {
        fps++;
        player.triggerKeys();

        if (!isPaused) {
            long currentTimeMillis = System.currentTimeMillis();
            long deltaTime = currentTimeMillis - lastTime;

            this.currentTime += deltaTime;
            this.deltaTime = deltaTime / 1_000.0; // Conversion en secondes

            lastTime = currentTimeMillis;

            scheduler.onUpdate();
            player.getPosition().room().onUpdate();
        }

        if (forceUpdate) {
            if (updateZone != null) {
                player.getUserInterface().repaint(
                        (int) updateZone.getX(),
                        (int) updateZone.getY(),
                        (int) updateZone.getWidth(),
                        (int) updateZone.getHeight()
                );
                updateZone = null;
            } else {
                player.getUserInterface().repaint();
            }

            forceUpdate = false;
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
        if (this.forceUpdate) {
            if (this.updateZone == null) return;
            if (zone == null) {
                this.updateZone = null;
                return;
            }

            this.updateZone = zone.union(this.updateZone);
            return;
        }

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