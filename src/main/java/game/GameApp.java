package game;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import game.component.player.PlayerInputKey;
import game.entity.EntityFactory;
import game.entity.EntityType;
import game.event.SeasonChangeEvent;
import game.player.ui.layer.GameLayer;
import game.world.World;
import game.world.WorldManager;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class GameApp extends GameApplication {

    private static GameApp instance;

    public static GameApp getInstance() {
        return instance;
    }

    private World world;
    private Season season = Season.SPRING;

    public World getWorld() {
        return world;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
        FXGL.getEventBus().fireEvent(new SeasonChangeEvent(season));
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Mon jeu avec UI FXGL");
        settings.setFullScreenFromStart(true);
        settings.setFullScreenAllowed(true);
    }

    @Override
    protected void initUI() {
        // Créer un conteneur vertical (VBox) pour l'UI
        VBox uiBox = new VBox(10);
        uiBox.setTranslateX(20);
        uiBox.setTranslateY(20);
        uiBox.setBackground(new Background(
                new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)
        ));

        // Ajouter un texte simple
        Text scoreText = new Text("Score: 0");
        scoreText.setFill(Color.WHITE);

        // Ajouter un bouton par exemple
        Button btnPause = new Button("Pause");
        btnPause.setOnAction(e -> {
            // Logique pause ici
            FXGL.getGameController().pauseEngine();
        });

        uiBox.getChildren().addAll(scoreText, btnPause);

        // Ajouter à la couche UI de FXGL (au-dessus du jeu)
        //FXGL.addUINode(new SettingsOverlayFXGL(), 100, 100);
    }

    @Override
    protected void initGame() {
        instance = this;

        FXGL.getGameWorld().addEntityFactory(new EntityFactory());

        var player = FXGL.spawn("player", 400, 400);
        if (!(player.hasComponent(PlayerInputKey.class))) {
            throw new RuntimeException("Impossible de créer le joueur");
        }

        var worldManager = new WorldManager();
        world = worldManager.getWorld("exteriorWorld");

        FXGL.spawn("border");

        // Dans GameApp.java, par exemple dans initUI()
        GameLayer gameLayer = new GameLayer(player);
        FXGL.getGameScene().addUINode(gameLayer);

        FXGL.getGameScene().getViewport().bindToEntity(player, 400, 400);
    }

    @Override
    protected void initPhysics() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.BORDER) {
            @Override
            protected void onCollisionBegin(Entity player, Entity border) {
                System.out.println("Collision !");
            }
        });

        //FXGL.getPhysicsWorld().addCollisionHandler(new SensorCollisionHandler(EntityType.PLAYER, EntityType.BORDER) {});
    }

    public static void main(String[] args) {
        launch(args);
    }
}

