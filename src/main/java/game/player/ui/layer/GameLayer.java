package game.player.ui.layer;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.Texture;
import game.GameApp;
import game.object.Cell;
import game.object.TileStateWithPos;
import game.util.Utils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameLayer extends Pane {

    private final Entity entity;
    private final Canvas canvas;

    public GameLayer(Entity entity) {
        this.entity = entity;
        this.canvas = new Canvas(FXGL.getAppWidth(), FXGL.getAppHeight());
        getChildren().add(canvas);
    }

    public void render() {
        for (var values : GameApp.getInstance().getWorld().getTiles().values()) {
            for (var tileSet : values.values()) {
                for (TileStateWithPos tile : tileSet) {
                    if (tile != null) {
                        var pos = tile.cell().toPixel();
                        var entity = tile.tile();
                        entity.translate(pos.x(), pos.y());
                        entity.getViewComponent().addChild(new Rectangle(48, 48, Color.BLACK));
                        entity.removeFromWorld();
                        entity.setVisible(true);
                        FXGL.getGameWorld().addEntity(entity);
                    }
                }
            }
        }
    }
}