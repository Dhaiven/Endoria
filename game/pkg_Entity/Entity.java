package game.pkg_Entity;

import game.GameEngineV2;
import game.pkg_Image.StaticSprite;
import game.pkg_Object.PlaceableGameObject;
import game.pkg_Object.Position;
import game.pkg_Object.TileStateWithPos;
import game.pkg_Object.Vector2;
import game.pkg_Room.Door;
import game.pkg_Util.MathUtils;
import game.pkg_Util.Utils;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import static game.pkg_Tile.CollisionType.*;

public class Entity extends PlaceableGameObject {

    protected FacingDirection facing;
    protected final Rectangle2D rigidBody2D;

    protected Position lastPosition = null;

    protected boolean isSpawned = false;

    public Entity(StaticSprite sprite, Position position, int layer) {
        this(sprite, new Rectangle2D.Double(0, 0, sprite.getWidth(), sprite.getHeight()), position, layer, FacingDirection.NORTH);
    }

    public Entity(StaticSprite sprite, Rectangle2D rigidBody2D, Position position, int layer) {
        this(sprite, rigidBody2D, position, layer, FacingDirection.NORTH);
    }

    public Entity(StaticSprite sprite, Rectangle2D rigidBody2D, Position position, int layer, FacingDirection facing) {
        super(sprite, position, layer);
        this.rigidBody2D = rigidBody2D;
        this.facing = facing;
    }

    public int getLayer() {
        return layer;
    }

    public void previousLayer() {
        if (position.room().getLayers().contains(layer - 1)) {
            this.layer -= 1;
        }
    }

    public void nextLayer() {
        if (position.room().getLayers().contains(layer + 1)) {
            this.layer += 1;
        }
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
        return this.rigidBody2D;
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

        var oldTile = position.room().getHighestTileAt(convertToTileSystem(position.vector2()));

        Vector2 newPosition = new Vector2(
                position.x() + deltaPosition.x(),
                position.y() + deltaPosition.y()
        );
        var newTile = position.room().getHighestTileAt(convertToTileSystem(newPosition));

        /**if (oldTile == newTile) {
            boolean canMove = true;
            for (TileBehavior behavior : oldTile.tile().getBehaviors()) {
                if (!behavior.canMove(oldTile, this)) {
                    canMove = false;
                }
            }

            if (!canMove) {
                return;
            }
        } else {
            boolean canChangeTile = true;
            for (TileBehavior behavior : oldTile.tile().getBehaviors()) {
                if (!behavior.canChangeTile(oldTile, newTile, this)) {
                    canChangeTile = false;
                }
            }
            for (TileBehavior behavior : newTile.tile().getBehaviors()) {
                if (!behavior.canChangeTile(oldTile, newTile, this)) {
                    canChangeTile = false;
                }
            }

            if (!canChangeTile) {
                return;
            }
        }*/

        this.lastPosition = position;

        Door exit = position.room().getExit(newPosition, facing);

        if (exit != null) {
            this.onChangeRoom(exit);
        } else {
            this.position = new Position(newPosition, this.position.room());
        }

        if (oldTile == newTile) {
            oldTile.tile().getBehaviors().forEach(behavior -> {
                behavior.onEntityMove(oldTile, this);
            });
        } else {
            oldTile.tile().getBehaviors().forEach(behavior -> {
                behavior.onChangeTile(oldTile, newTile, this);
            });

            newTile.tile().getBehaviors().forEach(behavior -> {
                behavior.onChangeTile(oldTile, newTile, this);
            });
        }

        GameEngineV2.getInstance().forceUpdate();
    }

    private Vector2 convertToTileSystem(Vector2 position) {
        return new Vector2(
                (int) Utils.TEXTURE_WIDTH * (int) (position.x() / Utils.TEXTURE_WIDTH),
                (int) Utils.TEXTURE_HEIGHT * (int) (position.y() / Utils.TEXTURE_HEIGHT)
        );
    }

    private Vector2 checkCollision(Vector2 deltaPosition, FacingDirection direction) {
        Rectangle2D rigidBody2D = getRigidBody2D().getBounds2D();
        rigidBody2D = new Rectangle2D.Double(position.x() - sprite.getWidth() / 2d, position.y() - sprite.getHeight(), rigidBody2D.getMaxX(), rigidBody2D.getMaxY());

        Line2D rayCasting = getRayCastFromRectangleSide(rigidBody2D, direction, deltaPosition);

        /**
         * TODO: custom shape for room
         */
        if (!position.room().contains(new Vector2(rayCasting.getX2(), rayCasting.getY2()))) {
            Double distance = MathUtils.distance(rigidBody2D, getRectangleSide(position.room().getArea().getBounds(), direction), direction);

            if (distance != null && distance < Math.abs(direction.getVectorComponent(deltaPosition))) {
                if (distance > Utils.COLLISION_RADIUS) {
                    return direction.getOffset(distance);
                }

                return null;
            }
        }

        for (List<TileStateWithPos> states : position.room().getTiles()) {
            for (TileStateWithPos state : states) {
                if (!state.tile().hasCollisions()) continue;

                if (switch (state.tile().getCollisionType()) {
                    case IF_SAME_LAYER -> state.layer() != layer;
                    case IF_DIFFERENT_LAYER -> state.layer() == layer;
                    case IF_ADJACENT_LAYER -> Math.abs(state.layer() - layer) != 1;
                    case IF_HIGHEST_AND_DIFFERENT_LAYER -> {
                        if (state.layer() == layer) {
                            yield true;
                        }
                        yield position.room().getHighestTileAt(state.position().vector2()).layer() == layer;
                    }
                    default -> false;
                }) {
                    continue;
                }

                for (Shape collision : state.tile().getCollisions()) {
                    Rectangle2D playerCollision = getRigidBody2D();
                    playerCollision = new Rectangle2D.Double(
                            position.x() - sprite.getWidth() / 2d + playerCollision.getX() - state.position().x(),
                            position.y() - sprite.getHeight() + playerCollision.getY() - state.position().y(),
                            playerCollision.getWidth(),
                            playerCollision.getHeight()
                    );

                    Double distance = MathUtils.distance(playerCollision, collision, direction);
                    if (distance != null && distance < Math.abs(direction.getVectorComponent(deltaPosition))) {
                        state.tile().getBehaviors().forEach(behavior -> behavior.onEntityCollide(state, this));

                        if (distance > Utils.COLLISION_RADIUS) {
                            return direction.getOffset(distance);
                        }

                        return null;
                    }
                }
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

    @Override
    public void paint(Graphics2D g2d) {
        super.paint(g2d);
        Rectangle2D playerCollision = getRigidBody2D();
        playerCollision = new Rectangle2D.Double(
                position.x() - sprite.getWidth() / 2d + playerCollision.getX(),
                position.y() - sprite.getHeight() + playerCollision.getY(),
                playerCollision.getWidth(),
                playerCollision.getHeight()
        );

        g2d.setColor(Color.BLACK);
        //g2d.fill(playerCollision);
    }
}
