package game.entity;

import game.GameEngine;

import game.image.Sprite;
import game.object.PlaceableGameObject;
import game.object.Position;
import game.object.Vector2;
import game.object.Vector2i;
import game.tile.Collision;
import game.util.MathUtils;
import game.util.Utils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

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
        if (position.world().layerExist(layer - 1)) {
            this.layer -= 1;
        }
    }

    public void nextLayer() {
        if (position.world().layerExist(layer + 1)) {
            this.layer += 1;
        }
    }

    public FacingDirection getFacing() {
        return facing;
    }

    public void spawn() {
        isSpawned = true;
        position.world().addEntity(this);
    }

    public void despawn() {
        position.world().removeEntity(this);
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
        move(facing, getMovementFactorPerSecond(facing) * GameEngine.getInstance().getDeltaTime());
    }

    public void move(FacingDirection facing, double movementFactor) {
        Vector2 deltaPosition = checkCollision(facing.getOffset(movementFactor), facing);
        if (deltaPosition == null) {
            return;
        }

        var oldTile = position.world().getHighestTileAt(position.vector2().toCell());

        Vector2 newPosition = new Vector2(
                position.x() + deltaPosition.x(),
                position.y() + deltaPosition.y()
        );
        var newTile = position.world().getHighestTileAt(newPosition.toCell());

        this.lastPosition = position;

        this.position = new Position(newPosition, this.position.world());

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

        GameEngine.getInstance().forceUpdate(
                getMinimalRepaintArea(lastPosition.vector2(), position.vector2(), facing, Utils.TEXTURE_SIZE.x(), Utils.TEXTURE_SIZE.y())
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

        var r = new Rectangle(minX, minY, width, height);
        var nR = new Rectangle(newX, newY, width, height);
        return r.union(nR);

        // Élargissement du rectangle dans la direction du mouvement

        /**switch (direction) {
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

        return new Rectangle(minX, minY, width, height);*/
    }

    private Vector2 checkCollision(Vector2 deltaPosition, FacingDirection direction) {
        Rectangle2D rigidBody2D = getRigidBody2D();

        Vector2 newPosition = new Vector2(
                position.x() + deltaPosition.x(),
                position.y() + deltaPosition.y()
        );
        
        Rectangle2D collisionMustBeCheckInView = new Rectangle2D.Double(
                Math.min(position.x(), newPosition.x()) - sprite.getWidth() / 2d,
                Math.min(position.y(), newPosition.y()) - sprite.getHeight(),
                rigidBody2D.getMaxX() + Math.abs(position.x() - newPosition.x()) + sprite.getWidth() / 2d,
                rigidBody2D.getMaxY()
        );

        for (var e : position.world().getTilesInView(collisionMustBeCheckInView)) {
            for (var state : e) {
                if (!state.tile().hasCollisions()) continue;

                Vector2i tileVector = state.cell().toPixel();
                Rectangle2D playerCollision = new Rectangle2D.Double(
                        position.x() - sprite.getWidth() / 2d + rigidBody2D.getX() - tileVector.x(),
                        position.y() - sprite.getHeight() + rigidBody2D.getY() - tileVector.y(),
                        rigidBody2D.getWidth(),
                        rigidBody2D.getHeight()
                );

                for (Collision collision : state.tile().getCollisions()) {
                    if (switch (collision.type()) {
                        case IF_SAME_LAYER -> state.layer().layer() != layer;
                        case IF_DIFFERENT_LAYER -> state.layer().layer() == layer;
                        case IF_ADJACENT_LAYER -> Math.abs(state.layer().layer() - layer) != 1;
                        case IF_HIGHEST -> {
                            var highestTile = position.world().getHighestTileAt(state.cell());
                            yield highestTile.layer().layer() != state.layer().layer();
                        }
                        case IF_HIGHEST_SUBLAYER -> {
                            var highestTile = position.world().getHighestTileAt(state.cell());
                            yield highestTile.layer().layer() != state.layer().layer() ||
                                    highestTile.layer().subLayer() != state.layer().subLayer();
                        }
                        case IF_HIGHEST_AND_DIFFERENT_LAYER -> {
                            if (state.layer().layer() == layer) {
                                yield true;
                            }
                            yield position.world().getHighestTileAt(state.cell()).layer().layer() == layer;
                        }
                        default -> false;
                    }) {
                        continue;
                    }

                    Double distance = MathUtils.distance(playerCollision, collision.shape(), direction);
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

    public void onUpdate() { }
}
