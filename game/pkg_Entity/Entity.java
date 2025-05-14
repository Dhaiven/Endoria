package game.pkg_Entity;

import game.GameEngineV2;

import game.pkg_Image.Sprite;
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
import java.util.Observer;

import static game.pkg_Tile.CollisionType.*;

public class Entity extends PlaceableGameObject {

    protected FacingDirection facing;
    protected final Rectangle2D rigidBody2D;

    protected Position lastPosition = null;

    protected boolean isSpawned = false;

    public Entity(Sprite sprite, Position position, int layer) {
        this(sprite, new Rectangle2D.Double(0, 0, sprite.getWidth(), sprite.getHeight()), position, layer, FacingDirection.NORTH);
    }

    public Entity(Sprite sprite, Rectangle2D rigidBody2D, Position position, int layer) {
        this(sprite, rigidBody2D, position, layer, FacingDirection.NORTH);
    }

    public Entity(Sprite sprite, Rectangle2D rigidBody2D, Position position, int layer, FacingDirection facing) {
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
            System.out.println("previousLayer " + layer);
        }
    }

    public void nextLayer() {
        if (position.room().getLayers().contains(layer + 1)) {
            this.layer += 1;
            System.out.println("nextLayer " + layer);
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
        return 205;
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
                behavior.onChangeTile(oldTile, newTile, this, true);
            });

            newTile.tile().getBehaviors().forEach(behavior -> {
                behavior.onChangeTile(oldTile, newTile, this, false);
            });
        }

        GameEngineV2.getInstance().forceUpdate(getMinimalRepaintArea(lastPosition.vector2(), position.vector2(), facing, 128, 128));
    }

    /**
     * Repeint la zone minimale englobant l'ancienne et la nouvelle position de l'entité.
     * Cette fonction prend en compte un point d'ancrage situé en bas au milieu de l'entité.
     *
     * @param oldPosition L'ancienne position de l'entité (Vector2), point d'ancrage en bas au milieu
     * @param newPosition La nouvelle position de l'entité (Vector2), point d'ancrage en bas au milieu
     * @param direction La direction vers laquelle l'entité est orientée (FacingDirection)
     * @param entityWidth La largeur de l'entité
     * @param entityHeight La hauteur de l'entité
     * @return Le Rectangle minimal à repeindre
     */
    public Rectangle getMinimalRepaintArea(Vector2 oldPosition, Vector2 newPosition,
                                           FacingDirection direction,
                                           int entityWidth, int entityHeight) {

        int padding = 5;

        // Calcul des coordonnées en haut à gauche pour chaque position
        // Comme le point d'ancrage est en bas au milieu, on soustrait la hauteur entière
        // et la moitié de la largeur pour obtenir le coin supérieur gauche
        int oldX = (int) (oldPosition.x() - (entityWidth / 2d) - padding);
        int oldY = (int) (oldPosition.y() - entityHeight - padding);
        int newX = (int) (newPosition.x() - (entityWidth / 2d) - padding);
        int newY = (int) (newPosition.y() - entityHeight - padding);

        // Calcul du rectangle contenant les deux positions
        int minX = Math.min(oldX, newX);
        int minY = Math.min(oldY, newY);
        int maxX = Math.max(oldX + entityWidth + (padding * 2), newX + entityWidth + (padding * 2));
        int maxY = Math.max(oldY + entityHeight + (padding * 2), newY + entityHeight + (padding * 2));

        int width = maxX - minX;
        int height = maxY - minY;

        // Élargissement du rectangle dans la direction du mouvement

        switch (direction) {
            case NORTH:
                // Élargit vers le haut (direction du regard)
                minY -= padding;
                height += padding;
                break;
            case SOUTH:
                // Élargit vers le bas (direction du regard)
                height += padding;
                break;
            case EAST:
                // Élargit vers la droite (direction du regard)
                width += padding;
                break;
            case WEST:
                // Élargit vers la gauche (direction du regard)
                minX -= padding;
                width += padding;
                break;
        }

        return new Rectangle(minX, minY, width, height);
    }

    // Calculer la région à repeindre
    private Rectangle calculateDirtyRegion(Vector2 oldPosition, Vector2 newPosition, FacingDirection direction) {
        // Ajuster en fonction de la direction
        double diffX = Math.ceil(Math.abs(oldPosition.x() - newPosition.x()));
        double diffY = Math.ceil(Math.abs(oldPosition.y() - newPosition.y()));

        System.out.println(diffX + " " + diffY);
        return switch (direction) {
            case NORTH -> new Rectangle(
                    (int) (oldPosition.x() - sprite.getWidth() / 2d),
                    (int) (newPosition.y() - sprite.getHeight()),
                    sprite.getWidth(),
                    (int) (sprite.getHeight() + diffY)
            );
            case SOUTH -> new Rectangle(
                    (int) (oldPosition.x() - sprite.getWidth() / 2d),
                    (int) (oldPosition.y() - sprite.getHeight()),
                    (int) (sprite.getWidth() + diffX),
                    (int) (sprite.getHeight() + oldPosition.y() - newPosition.y())
            );
            case EAST -> new Rectangle(
                    (int) (oldPosition.x() - sprite.getWidth() / 2d),
                    (int) (oldPosition.y() - sprite.getHeight() - 2),
                    (int) (sprite.getWidth()) + 10,
                    sprite.getHeight() + 10
            );
            case WEST -> new Rectangle(
                    (int) (newPosition.x() - sprite.getWidth() / 2d),
                    (int) (oldPosition.y() - sprite.getHeight()),
                    (int) (sprite.getWidth() + 10),
                    sprite.getHeight()
            );
        };
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
                    case IF_HIGHEST -> {
                        var highestTile = position.room().getHighestTileAt(state.position().vector2());
                        yield highestTile.layer() != state.layer() ||
                                highestTile.tile().getId() != state.tile().getId();
                    }
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

                Rectangle2D playerCollision = getRigidBody2D();
                playerCollision = new Rectangle2D.Double(
                        position.x() - sprite.getWidth() / 2d + playerCollision.getX() - state.position().x(),
                        position.y() - sprite.getHeight() + playerCollision.getY() - state.position().y(),
                        playerCollision.getWidth(),
                        playerCollision.getHeight()
                );
                for (Shape collision : state.tile().getCollisions()) {
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
}
