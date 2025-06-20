package game.entity;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.Texture;
import game.Season;
import game.component.EntityMovement;
import game.component.player.PlayerInputKey;
import game.component.tile.SeasonalTexture;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EntityFactory implements com.almasb.fxgl.entity.EntityFactory {

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        Texture texture = FXGL.getAssetLoader().loadTexture("playerIdle1.png");

        var physic = new PhysicsComponent();
        var bodyDef = new BodyDef();
        bodyDef.setGravityScale(0);
        bodyDef.setType(BodyType.DYNAMIC);
        physic.setBodyDef(bodyDef);

        return FXGL.entityBuilder(data)
                .type(EntityType.PLAYER)
                .viewWithBBox(new Rectangle(40, 40, Color.RED))
                //.view(texture)
                .bbox(new HitBox(BoundingShape.box(48, 10)))
                .with(physic)
                .collidable()
                .with(new EntityMovement(200))
                .with(new PlayerInputKey())
                .zIndex(100)
                .buildAndAttach();
    }

    @Spawns("border")
    public Entity newBorder(SpawnData data) {
        var physic = new PhysicsComponent();
        physic.setBodyType(BodyType.STATIC);
        physic.setFixtureDef(new FixtureDef().sensor(true));

        return FXGL.entityBuilder()
                .type(EntityType.BORDER)
                .at(0, 0)
                //.bbox(GameApp.getInstancqze().getWorld().getDimension())
                .viewWithBBox(new Rectangle(40, 40, Color.BLUE))
                .with(physic)
                .with(new CollidableComponent(false))
                .zIndex(100)
                .buildAndAttach();
    }

    @Spawns("tile")
    public Entity newTile(SpawnData data) {
        Map<Season, Supplier<Texture>> seasonalTextures = new HashMap<>(data.get("seasonalTextures"));

        return FXGL.entityBuilder(data)
                .type(EntityType.TILE)
                .with(new SeasonalTexture(seasonalTextures))
                .buildAndAttach();
    }
}
