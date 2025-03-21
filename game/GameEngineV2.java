package game;

import game.pkg_Command.CommandManager;
import game.pkg_Entity.pkg_Player.Player;
import game.pkg_Entity.pkg_Player.UserInterface;
import game.pkg_Image.StaticSprite;
import game.pkg_Scheduler.Scheduler;
import game.pkg_Util.FileUtils;
import game.pkg_World.WorldManager;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class GameEngineV2 implements Runnable {

    private static GameEngineV2 instance;

    private final Scheduler scheduler = new Scheduler();
    private final Player player;

    private double lastTime;
    private double deltaTime;

    private boolean isPaused = false;

    private boolean needToUpdate = false;

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

    public double getDelatTime() {
        return deltaTime;
    }

    public void setNeedToUpdate(boolean needToUpdate) {
        this.needToUpdate = needToUpdate;
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
    }

    @Override
    public void run() {
        lastTime = System.nanoTime();

        player.spawn();
        player.getUserInterface().repaint();

        while (true) {
            long currentTime = System.nanoTime();
            deltaTime = (currentTime - lastTime) / 1_000_000_000.0; // Convert to seconds
            lastTime = currentTime;

            if (!isPaused) {
                boolean hasUpdate = scheduler.onUpdate() || needToUpdate;

                if (player.getPosition().room().onUpdate() || hasUpdate) {
                    player.getUserInterface().repaint();
                }
            }

            try {
                Thread.sleep(1); // Pause de 1 milliseconde pour éviter d'exploser le CPU
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}