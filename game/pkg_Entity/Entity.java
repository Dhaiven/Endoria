package game.pkg_Entity;

import game.GameEngineV2;
import game.pkg_Image.StaticSprite;
import game.pkg_Object.PlaceableGameObject;
import game.pkg_Object.Position;
import game.pkg_Object.TileStateWithPos;
import game.pkg_Object.Vector2;
import game.pkg_Room.Door;
import game.pkg_Tile.Tile;
import game.pkg_Util.MathUtils;
import game.pkg_Util.Utils;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class Entity extends PlaceableGameObject {

    protected FacingDirection facing;

    protected Position lastPosition = null;

    protected boolean isSpawned = false;

    public Entity(StaticSprite sprite, Position position, int layer) {
        this(sprite, position, layer, FacingDirection.NORTH);
    }

    public Entity(StaticSprite sprite, Position position, int layer, FacingDirection facing) {
        super(sprite, position, layer);
        this.facing = facing;
    }

    public int getLayer() {
        return layer;
    }

    public FacingDirection getFacing() {
        return facing;
    }

    public void spawn() {
        isSpawned = true;
        position.room().getEntities().add(this);
    }

    public void despawn() {
        position.room().getEntities().remove(this);
        isSpawned = false;
    }

    public Rectangle2D getRigidBody2D() {
        return new Rectangle2D.Double(position.x() - sprite.getWidth() / 2d, position.y() - sprite.getHeight() / 2d, sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public boolean canPaint(Graphics2D g2d) {
        return isSpawned;
    }

    public int getMovementFactorPerSecond(FacingDirection facing) {
        /**
         * TODO: changer en fonction de si on veut un mouvement plus rapide ou non
         * Actuellement c'est une valeur prise arbitrairement
         */
        return 200;
    }

    public void move(FacingDirection facing) {
        move(facing, getMovementFactorPerSecond(facing) * GameEngineV2.getInstance().getDeltaTime());
    }

    public void move(FacingDirection facing, double movementFactor) {
        Vector2 deltaPosition = checkCollision(facing.getOffset(movementFactor), facing);
        if (deltaPosition == null) {
            return;
        }

        Vector2 newPosition = new Vector2(
                position.x() + deltaPosition.x(),
                position.y() + deltaPosition.y()
        );
        this.lastPosition = position;

        Door exit = position.room().getExit(newPosition, facing);

        if (exit != null) {
            this.onChangeRoom(exit);
        } else {
            this.position = new Position(newPosition, this.position.room());
        }

        GameEngineV2.getInstance().forceUpdate();
    }

    private Vector2 checkCollision(Vector2 deltaPosition, FacingDirection direction) {
        Line2D rayCasting = getRayCastFromRectangleSide(getRigidBody2D(), direction, deltaPosition);
        Rectangle2D rigidBody2D = getRigidBody2D();

        /**
         * TODO: custom shape for room
         */
        if (!position.room().contains(new Vector2(rayCasting.getX2(), rayCasting.getY2()))) {
           Double distance = MathUtils.distance(getRigidBody2D(), getRectangleSide(position.room().getArea().getBounds(), direction), direction);

            if (distance != null && distance < Math.abs(direction.getVectorComponent(deltaPosition))) {
                if (distance > Utils.COLLISION_RADIUS) {
                    return direction.getOffset(distance);
                }

                return null;
            }
        }

        for (Map.Entry<TileStateWithPos, Tile> entry : position.room().getTiles().entrySet()) {
            Shape collisionShape = entry.getValue().getCollision();
            if (collisionShape == null) continue;

            Rectangle2D playerCollision = new Rectangle2D.Double(
                    rigidBody2D.getX() - entry.getKey().position().x(),
                    rigidBody2D.getY() - entry.getKey().position().y(),
                    rigidBody2D.getWidth(),
                    rigidBody2D.getHeight()
            );

            Double distance = MathUtils.distance(playerCollision, collisionShape, direction);
            if (distance != null && distance < Math.abs(direction.getVectorComponent(deltaPosition))) {
                entry.getValue().getBehaviors().forEach(behavior -> behavior.onEntityCollide(entry.getKey(), this));

                if (distance > Utils.COLLISION_RADIUS) {
                    return direction.getOffset(distance);
                }

                return null;
            }
        }

        return deltaPosition;
    }

    private Vector2 getSidedPosition(Rectangle2D rectangle, FacingDirection direction) {
        return switch (direction) {
            case NORTH -> new Vector2(rectangle.getCenterX(), rectangle.getMinY());
            case SOUTH -> new Vector2(rectangle.getCenterX(), rectangle.getMaxY());
            case EAST  -> new Vector2(rectangle.getMaxX(), rectangle.getCenterY());
            case WEST  -> new Vector2(rectangle.getMinX(), rectangle.getCenterY());
        };
    }

    private Line2D getRectangleSide(Rectangle2D rectangle, FacingDirection direction) {
        return switch (direction) {
            case NORTH -> new Line2D.Double(rectangle.getX(), rectangle.getY(), rectangle.getMaxX(), rectangle.getY());
            case SOUTH -> new Line2D.Double(rectangle.getX(), rectangle.getMaxY(), rectangle.getMaxX(), rectangle.getMaxY());
            case EAST  -> new Line2D.Double(rectangle.getMaxX(), rectangle.getY(), rectangle.getMaxX(), rectangle.getMaxY());
            case WEST  -> new Line2D.Double(rectangle.getX(), rectangle.getY(), rectangle.getX(), rectangle.getMaxY());
        };
    }

    private Line2D getRayCastFromRectangleSide(Rectangle2D rectangle, FacingDirection direction, Vector2 deltaPosition) {
        Vector2 start = getSidedPosition(rectangle, direction);

        // Créer le segment de raycast
        return new Line2D.Double(
                start.x(),
                start.y(),
                start.x() + deltaPosition.x(),
                start.y() + deltaPosition.y()
        );
    }

    public void onChangeRoom(Door byDoor) {
        this.position.room().getEntities().remove(this);

        this.position = new Position(byDoor.getSpawnPosition(), byDoor.getTo());

        this.position.room().getEntities().add(this);
    }

    public boolean onUpdate() {
        return false;
    }
}
