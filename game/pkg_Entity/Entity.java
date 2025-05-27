package game.pkg_Entity;

import game.GameEngineV2;

import game.pkg_Image.Sprite;
import game.pkg_Object.PlaceableGameObject;
import game.pkg_Object.Position;
import game.pkg_Object.TileStateWithPos;
import game.pkg_Object.Vector2;
import game.pkg_Player.Player;
import game.pkg_Room.Door;
import game.pkg_Room.Room;
import game.pkg_Util.MathUtils;
import game.pkg_Util.Utils;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class Entity extends PlaceableGameObject {

    private final String name;

    protected FacingDirection facing;
    protected final Rectangle2D rigidBody2D;

    protected Position lastPosition = null;

    protected boolean isSpawned = false;

    public Entity(String name, Sprite sprite, Position position, int layer) {
        this(name, sprite, new Rectangle2D.Double(0, 0, sprite.getWidth(), sprite.getHeight()), position, layer, FacingDirection.NORTH);
    }

    public Entity(String name, Sprite sprite, Rectangle2D rigidBody2D, Position position, int layer) {
        this(name, sprite, rigidBody2D, position, layer, FacingDirection.NORTH);
    }

    public Entity(String name, Sprite sprite, Rectangle2D rigidBody2D, Position position, int layer, FacingDirection facing) {
        super(sprite, position, layer);

        this.name = name;
        this.rigidBody2D = rigidBody2D;
        this.facing = facing;
    }

    public String getName() {
        return name;
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
        position.room().addEntity(this);
    }

    public void despawn() {
        position.room().removeEntity(this);
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

        var oldTile = position.room().getHighestTileAt(Utils.convertToTileSystem(position.vector2()));

        Vector2 newPosition = new Vector2(
                position.x() + deltaPosition.x(),
                position.y() + deltaPosition.y()
        );
        var newTile = position.room().getHighestTileAt(Utils.convertToTileSystem(newPosition));

        this.lastPosition = position;

        Door exit = position.room().getExit(newPosition, facing);

        if (exit != null) {
            this.onChangeRoom(exit);

            position = switch (facing) {
                case NORTH, SOUTH -> new Position(lastPosition.x(), position.y(), position.room());
                case EAST, WEST -> new Position(position.x(), lastPosition.y(), position.room());
            };
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

        GameEngineV2.getInstance().forceUpdate(
                getMinimalRepaintArea(lastPosition.vector2(), position.vector2(), facing, 128, 128)
        );
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



    private Vector2 checkCollision(Vector2 deltaPosition, FacingDirection direction) {
        Rectangle2D rigidBody2D = getRigidBody2D().getBounds2D();
        rigidBody2D = new Rectangle2D.Double(position.x() - sprite.getWidth() / 2d, position.y() - sprite.getHeight(), rigidBody2D.getMaxX(), rigidBody2D.getMaxY());

        Line2D rayCasting = Utils.getRayCastFromRectangleSide(rigidBody2D, direction, deltaPosition);

        /**
         * TODO: custom shape for room
         */
        /**if (!position.room().contains(new Vector2(rayCasting.getX2(), rayCasting.getY2()))) {
            Double distance = MathUtils.distance(rigidBody2D, getRectangleSide(position.room().getArea().getBounds(), direction), direction);

            if (distance != null && distance < Math.abs(direction.getVectorComponent(deltaPosition))) {
                if (distance > Utils.COLLISION_RADIUS) {
                    return direction.getOffset(distance);
                }

                return null;
            }
        }*/

        for (List<TileStateWithPos> states : position.room().getTiles()) {
            for (TileStateWithPos state : states) {
                if (!state.tile().hasCollisions()) continue;

                if (switch (state.tile().getCollisionType()) {
                    case IF_SAME_LAYER -> state.layer() != layer;
                    case IF_DIFFERENT_LAYER -> state.layer() == layer;
                    case IF_ADJACENT_LAYER -> Math.abs(state.layer() - layer) != 1;
                    case IF_HIGHEST -> {
                        var highestTile = position.room().getHighestTileAt(state.position().vector2());
                        yield highestTile.layer() != state.layer();
                    }
                    case IF_HIGHEST_SUBLAYER -> {
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

    public boolean onChangeRoom(Door byDoor) {
        this.position.room().removeEntity(this);
        this.lastPosition = this.position;

        this.position = new Position(byDoor.getSpawnPosition(), byDoor.getTo());

        this.position.room().addEntity(this);
        Room newRoom = position.room();

        for (Entity e : newRoom.getEntities()) {
            if (e instanceof Player) {
                GameEngineV2.getInstance().forceUpdate();
            }
        }

        return true;
    }

    public void onUpdate() { }
}
