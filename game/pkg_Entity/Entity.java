package game.pkg_Entity;

import game.GameEngineV2;
import game.pkg_Image.Sprite;
import game.pkg_Object.PlaceableGameObject;
import game.pkg_Object.Position;
import game.pkg_Object.TileStateWithPos;
import game.pkg_Object.Vector2;
import game.pkg_Room.Door;
import game.pkg_Tile.Tile;
import game.pkg_Util.MathUtils;
import game.pkg_Util.MathUtilsV2;
import game.pkg_Util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Entity extends PlaceableGameObject {

    private List<TileStateWithPos> test = new ArrayList<>();
    protected FacingDirection facing;

    protected Position lastPosition = null;

    protected boolean isSpawned = false;

    public Entity(JComponent paintedOn, Sprite sprite, Position position, int layer) {
        this(paintedOn, sprite, position, layer, FacingDirection.NORTH);
    }

    public Entity(JComponent paintedOn, Sprite sprite, Position position, int layer, FacingDirection facing) {
        super(paintedOn, sprite, position, layer);
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

        getPaintedOn().repaint((int) position.x(), (int) position.y(), sprite.getWidth(), sprite.getHeight());
    }

    public void despawn() {
        position.room().getEntities().remove(this);
        isSpawned = false;

        getPaintedOn().repaint((int) position.x(), (int) position.y(), sprite.getWidth(), sprite.getHeight());
    }

    public Rectangle2D getRigidBody2D() {
        return new Rectangle2D.Double(position.x() - sprite.getWidth() / 2d, position.y() - sprite.getHeight() / 2d, sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public boolean canPaint(Graphics2D g2d) {
        return isSpawned;
    }

    public int getMovementFactor(FacingDirection facing) {
        /**
         * TODO: changer en fonction de si on veut un mouvement plus rapide ou non
         * Actuellement c'est une valeur prise aléatoirement
         */
        return 200;
    }

    public void move(FacingDirection facing) {
        move(facing, getMovementFactor(facing) * GameEngineV2.getInstance().getDelatTime());
    }

    private Vector2 generateDeltaPosition(FacingDirection facing, double movementFactor) {
        return switch (facing) {
            case NORTH -> new Vector2(0, -movementFactor);
            case SOUTH -> new Vector2(0, movementFactor);
            case EAST -> new Vector2(movementFactor, 0);
            case WEST -> new Vector2(-movementFactor, 0);
        };
    }

    public void move(FacingDirection facing, double movementFactor) {
        Vector2 deltaPosition = checkCollision(generateDeltaPosition(facing, movementFactor), facing);
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
    }

    private Vector2 checkCollision(Vector2 deltaPosition, FacingDirection direction) {
        Line2D rayCasting = getRayCastFromRectangleSide(getRigidBody2D(), direction, deltaPosition);

        // Marche parfaitement pour tous les fps
        if (!position.room().contains(new Vector2(rayCasting.getX2(), rayCasting.getY2()))) {
            Double distance = MathUtils.distance(rayCasting, getRectangleSide(position.room().getArea().getBounds(), direction));

            if (distance != null && switch (direction) {
                case NORTH, SOUTH -> distance < Math.abs(deltaPosition.y());
                case EAST, WEST  -> distance < Math.abs(deltaPosition.x());
            }) {
                if (distance > Utils.COLLISION_RADIUS) {
                    return generateDeltaPosition(direction, distance);
                }

                return null;
            }
        }

        Rectangle2D rigidBody2D = getRigidBody2D();
        for (Map<Vector2, Tile> tileState : position.room().getTiles()) {
            for (Map.Entry<Vector2, Tile> entry : tileState.entrySet()) {
                Shape collisionShape = entry.getValue().getCollision();
                if (collisionShape == null) continue;

                Rectangle2D playerCollision = new Rectangle2D.Double(
                        rigidBody2D.getX() - entry.getKey().x(),
                        rigidBody2D.getY() - entry.getKey().y(),
                        rigidBody2D.getWidth(),
                        rigidBody2D.getHeight()
                );

                Double distance = MathUtilsV2.distance(playerCollision, collisionShape.getBounds(), direction);
                if (distance != null && switch (direction) {
                    case NORTH, SOUTH -> distance < Math.abs(deltaPosition.y());
                    case EAST, WEST -> distance < Math.abs(deltaPosition.x());
                }) {
                    if (distance > Utils.COLLISION_RADIUS) {
                        return generateDeltaPosition(direction, distance);
                    }

                    return null;
                }
            }
        }

        return deltaPosition;
    }

    private Vector2 getSidedPosition(Rectangle2D rectangle, FacingDirection direction) {
        Vector2 start = switch (direction) {
            case NORTH -> new Vector2(rectangle.getCenterX(), rectangle.getMinY());
            case SOUTH -> new Vector2(rectangle.getCenterX(), rectangle.getMaxY());
            case EAST  -> new Vector2(rectangle.getMaxX(), rectangle.getCenterY());
            case WEST  -> new Vector2(rectangle.getMinX(), rectangle.getCenterY());
        };

        return new Vector2(start.x(), start.y());
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

        g2d.setColor(Color.GREEN);
        for (var t : test) {
            var sprite = t.tile().getSprite();
            g2d.fillRect((int) t.position().x(), (int) t.position().y(), sprite.getWidth(), sprite.getHeight());
        }

        g2d.setColor(Color.WHITE);
        g2d.fill(getRigidBody2D());

        g2d.setColor(Color.RED);
        for (var t : position.room().getTiles()) {
            for (var entry : t.entrySet()) {
                var tile = entry.getValue();
                if (tile.getCollision() == null) continue;
                Rectangle e = tile.getCollision().getBounds();
                e.translate((int) entry.getKey().x(), (int) entry.getKey().y());
                g2d.fill(e);
            }
        }

        g2d.setColor(Color.BLUE);
        g2d.fillRect((int) position.x(), (int) position.y(), 1, 1);
    }
}
