package game;

import game.pkg_Command.CommandManager;
import game.pkg_Player.pkg_Action.Action;
import game.pkg_Player.Player;
import game.pkg_Player.UserInterface;
import game.pkg_Image.StaticSprite;
import game.pkg_Scheduler.Scheduler;
import game.pkg_Util.FileUtils;
import game.pkg_World.WorldManager;

import javax.imageio.ImageIO;
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

    private boolean isPaused = false;

    private boolean forceUpdate = false;

    public GameEngineV2() {
        instance = this;

        CommandManager commandManager = new CommandManager();

        try {
            var playerSprite = ImageIO.read(new File(FileUtils.ASSETS_RESOURCES + "player.png"));
            var worldManager = new WorldManager();

            player = new Player(player1 -> new UserInterface(player1, commandManager), new StaticSprite(playerSprite.getSubimage(0, 0, 64, 64)), worldManager.getWorld("museum").getSpawnRoom());
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

    public GameState[] getStates() {
        GameState[] result = new GameState[2];
        if (isPaused) {
            result[0] = GameState.PAUSE;
        } else {
            result[0] = GameState.PLAY;
        }

        result[1] = GameState.ALL;
        return result;
    }

    public void start() {
        lastTime = System.currentTimeMillis();

        player.spawn();
        player.getUserInterface().repaint();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        future = scheduler.scheduleWithFixedDelay(this, 0, 1, TimeUnit.MILLISECONDS);
    }

    // Méthode principale de la boucle de jeu
    @Override
    public void run() {
        player.triggerKeys();

        boolean hasUpdate = forceUpdate;
        if (!isPaused) {
            long currentTimeMillis = System.currentTimeMillis();
            long deltaTime = currentTimeMillis - lastTime;

            this.currentTime += deltaTime;
            this.deltaTime = deltaTime / 1_000.0; // Conversion en secondes

            lastTime = currentTimeMillis;

            hasUpdate = scheduler.onUpdate() || hasUpdate;
            hasUpdate = player.getPosition().room().onUpdate() || hasUpdate;
        }

        if (hasUpdate) {
            player.getUserInterface().repaint();
            forceUpdate = false;
        }
    }

    // Méthode pour arrêter complètement la boucle de jeu
    public void stop() {
        future.cancel(false);
        System.exit(0);
    }

    public void forceUpdate() {
        this.forceUpdate = true;
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
        forceUpdate();
    }
}