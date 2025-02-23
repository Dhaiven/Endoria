package game.pkg_Entity;

import game.pkg_Object.PlaceableGameObject;
import game.pkg_Object.Position;
import game.pkg_Object.Vector2;
import game.pkg_Room.Door;
import game.pkg_Room.Room;
import game.pkg_Util.Utils;

import javax.swing.*;
import java.awt.*;

public class Entity extends PlaceableGameObject {

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

        getPaintedOn().repaint(position.x(), position.y(), sprite.getWidth(), sprite.getHeight());
    }

    public void despawn() {
        position.room().getEntities().remove(this);
        isSpawned = false;

        getPaintedOn().repaint(position.x(), position.y(), sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public boolean canPaint(Graphics2D g2d) {
        return isSpawned;
    }

    public int getMovementFactor(FacingDirection facing) {
        /**
         * 7 est choisi arbitrairement
         * Permet un mouvement pas trop rapide ni trop lent
         */
        return switch (facing) {
            case NORTH, SOUTH -> sprite.getHeight();
            case EAST, WEST -> sprite.getWidth();
        } / 7;
    }

    public void move(FacingDirection facing) {
        this.lastPosition = position;

        int movementFactor = getMovementFactor(facing);
        Vector2 newPosition = switch (facing) {
            case NORTH -> new Vector2(position.x(), position.y() - movementFactor);
            case SOUTH -> new Vector2(position.x(), position.y() + movementFactor);
            case EAST -> new Vector2(position.x() + movementFactor, position.y());
            case WEST -> new Vector2(position.x() - movementFactor, position.y());
        };

        if (position.room().contains(newPosition)) {
            this.position = new Position(newPosition, this.position.room());
        } else {
            Door exit = position.room().getExit(newPosition, facing);
            if (exit == null) return;

            this.onChangeRoom(exit, facing);
        }

        this.paintedOn.repaint();
    }

    public void onChangeRoom(Door byDoor, FacingDirection direction) {
        this.position.room().getEntities().remove(this);

        this.position = new Position(Utils.getCenterPosition(byDoor.getShape(), direction), byDoor.getTo());

        this.position.room().getEntities().add(this);
    }

    protected void changeRoom(Room room) {
        this.position = new Position(position.x(), position.y(), room);
        // TODO: change position
    }
}
