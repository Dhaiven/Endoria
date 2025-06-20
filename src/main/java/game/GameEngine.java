package game;

import com.almasb.fxgl.texture.Texture;
import game.player.Player;
import game.player.ui.UserInterface;
import game.scheduler.Scheduler;
import game.tile.TileManager;
import game.util.FileUtils;
import game.world.WorldManager;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.IOException;

public class GameEngine {

    private static GameEngine instance;

    private final Scheduler scheduler = new Scheduler();
    private final TileManager tileManager = new TileManager();
    private Player player;

    private long currentTime = 0;
    private long lastTime = 0;
    private double deltaTime;

    private boolean isPaused = false;

    private GraphicsContext gc;

    private String alea = null;

    public GameEngine() {
        instance = this;

        try {
            Image playerSprite = new Image(new FileInputStream(FileUtils.ASSETS_RESOURCES + "game/player/idle/playerIdle2.png"));
            var worldManager = new WorldManager();

            player = new Player(
                    UserInterface::new,
                    new Texture(playerSprite),
                    new Rectangle2D.Double(0, 38, 48, 10),
                    worldManager.getWorld("exteriorWorld")
            );
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement des assets", e);
        }
    }

    public static GameEngine getInstance() {
        return instance;
    }

    public GameState getGameState() {
        return GameState.PLAY;
    }

    public void init(GraphicsContext gc) {
        this.gc = gc;
        this.lastTime = System.currentTimeMillis();
        //player.spawn();
    }

    public void update() {
        player.triggerKeys();

        if (!isPaused) {
            long now = System.currentTimeMillis();
            long dt = now - lastTime;
            lastTime = now;

            this.currentTime += dt;
            this.deltaTime = dt / 1000.0;

            scheduler.onUpdate();
            //player.getPosition().world().onUpdate();
        }
    }

    public void render() {
        // Efface l'écran
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // TODO : dessiner ici les tuiles, les entités, le joueur
        // Exemple :
        // gc.drawImage(sprite, posX, posY);

        //player.getUserInterface().render(gc);
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
        lastTime = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return player;
    }

    public Scheduler getScheduler() {
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

    public boolean isPaused() {
        return isPaused;
    }

    public String getAlea() {
        return alea;
    }

    public void setAlea(String alea) {
        this.alea = alea;
    }
}
